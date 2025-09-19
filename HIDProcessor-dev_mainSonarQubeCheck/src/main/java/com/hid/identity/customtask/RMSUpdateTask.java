package com.hid.identity.customtask;

import java.util.HashMap;
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
import com.konylabs.middleware.dataobject.Result;

public class RMSUpdateTask implements IdentityTask{
	private String environmentId = "";
	private String channelId = "";
	private String applicationId = "";

	@SuppressWarnings({"java:S3516"})
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) {
		String mfaMetaObj = request.getAttribute(IdentityConstants.META_OBJECT_ID);
		String username = Objects.toString(inputMap.get("username"));
		String authType = Objects.toString(inputMap.get("authType"));
		mfaMetaObj = mfaMetaObj == null ? "{}" : mfaMetaObj;
		JSONObject authJsonObj = new JSONObject(mfaMetaObj);
		if (!StringUtils.isEmpty(mfaMetaObj)) {
			String appSessionId = authJsonObj.optString("appSessionId", "");
			if (appSessionId.isEmpty()) {
				log("RMS is not present");
				return true;
			}

		}
		String platform = authJsonObj.optString("platform", "");
		String tmActionId = authJsonObj.optString("tm_action_id", "");
		validateAndPopulateRMSConstants(request, platform, result);
		LoginRMSApi loginRMSApi = new LoginRMSApi();
		HIDRMSDataclass hidrms  = getRMSData(request, authJsonObj, authType);
		if(!tmActionId.isEmpty()) {
     		loginRMSApi.updateSuccessLogin(hidrms, hidrms.getAppSessionId(), username,tmActionId,request);
     		log("RMS login Updated");
		}
		return true;
	}
	
	@SuppressWarnings({"java:S1172"})
	private HIDRMSDataclass getRMSData(DataControllerRequest request, JSONObject authJsonObj,
			String authType) {
		String appSessionId = authJsonObj.optString("appSessionId", "");
		String tmSessionId = authJsonObj.optString("tmSessionId", "");
		String tmDeviceTag = authJsonObj.optString("tmDeviceTag", "");
		String clientIp = authJsonObj.optString("clientIp", "");
		HIDRMSDataclass hidrms;
		if(authType.equalsIgnoreCase("FIDO")) {
			 hidrms = new HIDRMSDataclass(applicationId, channelId, environmentId,tmDeviceTag, tmSessionId, clientIp,
					2, getSecurityItemType(authType), appSessionId, "FIDO");
		} else {
		    hidrms = new HIDRMSDataclass(applicationId, channelId, environmentId,tmDeviceTag, tmSessionId, clientIp,
				2, getSecurityItemType(authType), appSessionId, "Approve");
		}
		return hidrms;
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
