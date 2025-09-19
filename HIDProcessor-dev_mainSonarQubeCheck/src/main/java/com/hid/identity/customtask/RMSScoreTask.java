package com.hid.identity.customtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import com.hid.dataclasses.HIDRMSDataclass;
import com.hid.identity.util.IdentityConstants;
import com.hid.identity.util.IdentityLogger;
import com.hid.identity.util.ServiceUtils;
import com.hid.rmsservices.LoginRMSApi;
import com.hid.rmsservices.RMSUtils;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class RMSScoreTask implements IdentityTask, FailureValidatorTaskMarker {
	private int factor;
	private LoginRMSApi rmsApi;
	private String environmentId = "";
	private String channelId = "";
	private String applicationId = "";
	boolean isUpdateScore = false;
	boolean isBlockTag = false;

	private static final String APP_SESSION_ID_PARAM = "appSessionId";
	private static final String PLATFORM_PARAM = "platform";
	private static final String TM_TAG_PARAM = "tm_tag";
	private static final String TM_SID_PARAM = "tm_sid";	
	private static final String APP_SESSION_ID_PARAM_NEW = "app_session_id";
	private static final String STEP_UP_PARAM = "stepUp";
	private static final String RMS_SERVICE_STATUS_PARAM = "RMSServiceStatus";
	private static final String FAILED_PARAM = "failed";
	private static final String CURRENT_THREAT_PARAM = "currentThreat";
	private static final String NOT_PRESENT_PARAM = "not Present";

	public RMSScoreTask(int factor) {
		this.factor = factor;
		rmsApi = new LoginRMSApi();
	}

	
	@SuppressWarnings({"java:S3776" , "java:S1854" , "java:S1481" })
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) {
		String username = Objects.toString(inputMap.get("username"));
		String authType = Objects.toString(inputMap.get("authType"));
		if (factor == 2) {
			String mfaMetaObj = request.getAttribute(IdentityConstants.META_OBJECT_ID);
			mfaMetaObj = mfaMetaObj == null ? "{}" : mfaMetaObj;
			JSONObject authJsonObj = new JSONObject(mfaMetaObj);
			if (!StringUtils.isEmpty(mfaMetaObj)) {
				String appSessionId = authJsonObj.optString(APP_SESSION_ID_PARAM, "");
				if (appSessionId.isEmpty()) {
					log("RMS is not present");
					return true;
				}

			}
			if(authType.equals("NO_MFA") || authType.equals("STEP_DOWN")){
				log("RMS Scoring is not required ");
				return true;
			}
			String platform = authJsonObj.optString(PLATFORM_PARAM, "");
			if(!validateAndPopulateRMSConstants(request, platform, result)) return false;
			Result rmsResult = getRMSScore(getRMSDataForSecondFactor(request, authJsonObj, authType), username, request, 2 , authType);
			return true;
		} else {
			String payload = Objects.toString(inputMap.get("payload"), "{}");
			JSONObject payloadRmsJson = new JSONObject(payload);
			JSONObject metaLoad = payloadRmsJson.getJSONObject("Meta");
			if (metaLoad == null || !metaLoad.has("rmspayload")) {
				log("RMS is not present");
				return true;
			}
			JSONObject rmsLoad = metaLoad.getJSONObject("rmspayload");
			String platform = rmsLoad.optString(PLATFORM_PARAM, "");
			if(!validateAndPopulateRMSConstants(request, platform, result)) return false;
			HIDRMSDataclass hidrms = getRMSDataFirstFactor(rmsLoad, request, result, authType);
			Result rmsResult = getRMSScore(hidrms, username, request, factor, authType);
			log("HIDRMS::HIDRMSPayload rmsLoad " + rmsLoad.optString(TM_TAG_PARAM, NOT_PRESENT_PARAM) + " " + rmsLoad.optString(TM_SID_PARAM , NOT_PRESENT_PARAM ) + " "
					+ rmsLoad.get(APP_SESSION_ID_PARAM_NEW));
			if(isUpdateScore) {
				updateScoreToResult(rmsResult, result,username);
				updateMetaObject(hidrms, rmsResult, request, platform);
			}
			return !isBlockTag;
		}

	}
    private void updateMetaObject(HIDRMSDataclass hidrms, Result rmsResult, DataControllerRequest request, String platform){
    	JSONObject statusObj = new JSONObject();
    	statusObj.put(APP_SESSION_ID_PARAM, hidrms.getAppSessionId());
		statusObj.put("tmSessionId", hidrms.getTmSessionId());
		statusObj.put("tmDeviceTag", hidrms.getTmDeviceTag());
		statusObj.put("clientIp", hidrms.getClientIp());
		statusObj.put(PLATFORM_PARAM, platform);
		String tmActionIdResp = rmsResult.getParamValueByName("tm_action_id");
		if(!StringUtils.isEmpty(tmActionIdResp)) {statusObj.put("tm_action_id", tmActionIdResp);}
    	request.setAttribute(IdentityConstants.META_OBJECT_ID, statusObj.toString());
    }

	@SuppressWarnings({"java:S3776" })
	private void updateScoreToResult(Result rmsResult, Result mainResult, String username) {
		isBlockTag = false;
		Record rmsMeta = new Record();
		Record mfaMeta = new Record();
		rmsMeta.setId(IdentityConstants.RMS_META_RECORD_ID);
		mfaMeta.setId(IdentityConstants.MFA_META_RECORD_ID);
		mfaMeta.addRecord(rmsMeta);
		mainResult.addRecord(mfaMeta);
		if(rmsResult == null) {
			rmsMeta.addStringParam(STEP_UP_PARAM, "true");
			rmsMeta.addStringParam(RMS_SERVICE_STATUS_PARAM, FAILED_PARAM);
			return;
		}
		try {
			String stepTag = rmsResult.getParamValueByName("stepTag") == null ? "" : rmsResult.getParamValueByName("stepTag") ;
			String riskScore = rmsResult.getParamValueByName("risk");
			String currentThreat = rmsResult.getParamValueByName(CURRENT_THREAT_PARAM);
			boolean isSignalDetected = false;
			Dataset tagsDataSet = rmsResult.getDatasetById("tags");
			ArrayList<String> tagsArrayList = new ArrayList<>();
			if (tagsDataSet != null) {
				List<Record> tags = tagsDataSet.getAllRecords();
				boolean isEmptyDataSet = tags == null || tags.isEmpty();
				if(!isEmptyDataSet) {
					for(Record r : tags) {
			            tagsArrayList.add(r.getParamValueByName("tagAction"));
					}
				}
			}
			for(String tag :RMSUtils.getSignalTags()) {
				if(tagsArrayList.contains(tag)) {
				  rmsMeta.addStringParam(tag, "true");
				  isSignalDetected = true;
				}
			}
			if ( isSignalDetected || stepTag.equalsIgnoreCase(AuthenticationConstants.RMS_STEP_UP_TAG)) {
				rmsMeta.addStringParam(STEP_UP_PARAM, "true");
				rmsMeta.addStringParam(CURRENT_THREAT_PARAM, currentThreat);
				rmsMeta.addStringParam(RMS_SERVICE_STATUS_PARAM, "success");
				rmsMeta.addStringParam("riskScore", riskScore);
				log("HIDRMS :: Step-up is required and having tag as");
			}
			else if ( stepTag.equalsIgnoreCase(AuthenticationConstants.RMS_STEP_DOWN_TAG)){
				rmsMeta.addStringParam(STEP_UP_PARAM, "false");
				rmsMeta.addStringParam("riskScore", riskScore);
				rmsMeta.addStringParam(CURRENT_THREAT_PARAM, currentThreat);
				rmsMeta.addStringParam(RMS_SERVICE_STATUS_PARAM, "success");
			} 
			else if (stepTag.equalsIgnoreCase(AuthenticationConstants.RMS_BLOCK_TAG)) {
				isBlockTag = true;
				log("HIDRMS :: "+ username+ " is blocked by RMS for Login attempt, because of miscellaneous Activity");
				ServiceUtils.setErrorToResult(mainResult, AuthenticationConstants.RMS_BLOCK_TAG , -3, 401);
			}else {
				rmsMeta.addStringParam(STEP_UP_PARAM, "true");
				rmsMeta.addStringParam(RMS_SERVICE_STATUS_PARAM, FAILED_PARAM);
			}
		} catch (Exception e) {
			rmsMeta.addStringParam(STEP_UP_PARAM, "true");
			rmsMeta.addStringParam(RMS_SERVICE_STATUS_PARAM, FAILED_PARAM);
			rmsMeta.addStringParam("rms_exception", e.getMessage());
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings({"java:S1172"})
	private HIDRMSDataclass getRMSDataFirstFactor(JSONObject rmsLoad, DataControllerRequest request, Result result, String authType) {
			log("HIDRMS::HIDRMSPayload rmsLoad " + rmsLoad.optString(TM_TAG_PARAM,NOT_PRESENT_PARAM) + " " + rmsLoad.optString(TM_SID_PARAM, NOT_PRESENT_PARAM) + " "
					+ rmsLoad.optString(APP_SESSION_ID_PARAM_NEW,NOT_PRESENT_PARAM));
			String tmDeviceTag = rmsLoad.optString(TM_TAG_PARAM,"");
			String tmSessionId = rmsLoad.optString(TM_SID_PARAM,"");
			String clientIp = rmsLoad.optString("client_ip","");
			String appSessionId =  rmsLoad.optString(APP_SESSION_ID_PARAM_NEW,"");
			log("HIDRMS::HIDRMSPayload Server properties " + applicationId + " " + channelId);
			log("HIDRMS::HIDRMSPayload Server properties " + applicationId + " " + channelId);
			log("HIDRMS::HIDRMSPayload " + rmsLoad);
			return new HIDRMSDataclass(applicationId, channelId, environmentId, tmDeviceTag,
					tmSessionId, appSessionId, clientIp, getSecurityItemType(authType), "AT_CUSTOTP");
	}

	private Result getRMSScore(HIDRMSDataclass hidrms, String username, DataControllerRequest request,
	    int factor, String authType) {
		return rmsApi.getTag(hidrms, username, request, getLoginStepResult(request, authType), factor);
	}

	private String getLoginStepResult(DataControllerRequest request, String authType) {
		String validatorStatus = request.getAttribute(IdentityConstants.VALIDATOR_STATUS);
		if(!StringUtils.isEmpty(validatorStatus) && IdentityConstants.VALIDATOR_SUCCESS_PARAM.equals(validatorStatus)) {
			isUpdateScore = true;
			return AuthenticationConstants.LOGIN_STEP_SUCCESS;
		}
		isUpdateScore = false;
		if("APPROVE_DENY".equals(authType) || "APPROVE_TIMEOUT".equals(authType)){
			return AuthenticationConstants.LOGIN_STEP_DENIED;
		}
		return AuthenticationConstants.LOGIN_STEP_INVALID;
	}

	@SuppressWarnings({"java:S1172"})
	private HIDRMSDataclass getRMSDataForSecondFactor(DataControllerRequest request, JSONObject authJsonObj,
			String authType) {
		String appSessionId = authJsonObj.optString(APP_SESSION_ID_PARAM, "");
		String tmSessionId = authJsonObj.optString("tmSessionId", "");
		String tmDeviceTag = authJsonObj.optString("tmDeviceTag", "");
		String clientIp = authJsonObj.optString("clientIp", "");
		return new HIDRMSDataclass(applicationId, channelId, environmentId, tmDeviceTag, tmSessionId,
				appSessionId, clientIp, getSecurityItemType(authType), "AT_CUSTOTP");
	}

	private String getSecurityItemType(String authType) {
		switch (authType) {
		case AuthenticationConstants.SECURE_CODE_KEY:
			return "otp";
		case AuthenticationConstants.SMS_OTP_KEY:
			return "otp";
		case AuthenticationConstants.EMAIL_OTP_KEY:
			return "otp";
		case AuthenticationConstants.APPROVE_KEY:
			return "pki";
		case AuthenticationConstants.FIDO_KEY:
			return "password";
		case "APPROVE_DENY" :
			return "pki";
		case "APPROVE_TIMEOUT" :
			return "pki";
		default:
			return "password";
		}
	}
    private boolean validateAndPopulateRMSConstants(DataControllerRequest request, String platform, Result result) {
    	try {
			environmentId = GetConfProperties.getProperty(request, AuthenticationConstants.HID_RMS_ENVIRONMENT_ID_KEY);
			applicationId = RMSUtils.getApplicationId(request, platform);
			channelId = RMSUtils.getChannelId(request, platform);
		} catch (Exception e) {
			e.printStackTrace();
			ServiceUtils.setErrorToResult(result, AuthenticationConstants.EMPTY_APP_CHANNEL_ID, -1, 401);
			return false;
		}
    	if (applicationId.isEmpty() || channelId.isEmpty() || environmentId.isEmpty()) {
			ServiceUtils.setErrorToResult(result, AuthenticationConstants.EMPTY_APP_CHANNEL_ID, -1, 401);
			return false;
		}
    	return true;
    }
	private void log(String msg) {
		IdentityLogger.debug("RMSTask", this.getClass().getSimpleName(), msg);
	}
}
