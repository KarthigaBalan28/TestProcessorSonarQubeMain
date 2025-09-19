package com.hid.rmsservices;

import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hid.dataclasses.HIDRMSDataclass;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.hid.util.HIDIntegrationServices;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;

@SuppressWarnings("java:S2629")
public class LoginRMSApi {
	private static final Logger LOG = LogManager.getLogger(com.hid.rmsservices.LoginRMSApi.class);

	public Result getTag(HIDRMSDataclass hidrms, String username, DataControllerRequest request, String loginStepResult,
			Integer loginFactorIndex) {
		LOG.debug("HIDRMS : In LoginRMSApi");
		UUID uuid = UUID.randomUUID();
		String appActionId = uuid.toString();
		String action = "stepTag";
		HashMap<String, Object> bodyMap = RMSUtils.getLoginRequestBody(hidrms, username, loginStepResult,
				loginFactorIndex, appActionId);
		try {
			Result result = HIDIntegrationServices.call("HIDRMSThreatMarkAPI", "sessionCreate", request,
					new HashMap<String, Object>(), bodyMap);
			String appSessionId = hidrms.getAppSessionId();
			result.addStringParam("appSessionId", appSessionId);
			LOG.debug("HIDRMS : HIDRMSThreatMarkAPI =>  sessionCreate, username:{} appSessionId:{} result:{}", username, appSessionId, ResultToJSON.convert(result));
			String isCustomMapFromProp = GetConfProperties.getProperty(request, "IS_CUSTOM_RMS_SCORE_RANGE");
			boolean isCustomMap = isCustomMapFromProp.equalsIgnoreCase("true");
			String scoreMapString = AuthenticationConstants.RMS_DEFAULT_SCORE_MAP;
			if (isCustomMap) {
				String customMapString = GetConfProperties.getProperty(request, "CUSTOM_RMS_SCORE_MAP");
				scoreMapString = "".equals(customMapString) ? scoreMapString : customMapString;
			}
			String riskScore = result.getParamValueByName("risk");
			LOG.debug("HIDRMS : username:{} appSessionId:{} riskScore:{}", username, appSessionId, riskScore);
			RMSScoreMapper mapper = new RMSScoreMapper(isCustomMap, scoreMapString);
			Integer infinityScore = riskScore != null ? mapper.getMappedScore(Integer.parseInt(riskScore)) : -2;
			LOG.debug("HIDRMS : username:{} appSessionId:{} infinityScore:{}", username, appSessionId, infinityScore);
			result.addIntParam("currentThreat", infinityScore);
			String resultString = ResultToJSON.convert(result);
			LOG.debug("HIDRMS : Result String is:{}", result);
			if (checkForTag(AuthenticationConstants.RMS_BLOCK_TAG, resultString)) {
				result.addStringParam(action, AuthenticationConstants.RMS_BLOCK_TAG);
				return result;
			}else if (checkForTag(AuthenticationConstants.RMS_STEP_UP_TAG, resultString)) {
				result.addStringParam(action, AuthenticationConstants.RMS_STEP_UP_TAG);
				return result;
			} else if (checkForTag(AuthenticationConstants.RMS_STEP_DOWN_TAG, resultString)) {
				result.addStringParam(action, AuthenticationConstants.RMS_STEP_DOWN_TAG);
				return result;
			} 
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			Result result1 = new Result();
			result1.addStringParam("exception", "Exception in Service call");
			LOG.error("HIDRMS : Exception in Service call, error message: ", e);
			return result1;
		}
	}

	public void updateSuccessLogin(HIDRMSDataclass hidrms, String appSessionId, String username, String tmActionId,
			DataControllerRequest request) {
		HashMap<String, Object> bodyMap = RMSUtils.getLoginSuccessRequestBody(hidrms, username, tmActionId,
				appSessionId);
		try {
			HIDIntegrationServices.call("HIDRMSThreatMarkAPI", "sessionLogin", request, null, bodyMap);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("HIDRMS : Exception HIDRMSThreatMarkAPI => sessionLogin, error message: ", e);
		}
	}

	public boolean checkForTag(String tag, String result) {
		JSONObject obj = new JSONObject(result);
		JSONArray tags = obj.getJSONArray("tags");
		if(tags.length() == 0) {
			LOG.debug("HIDRMS : ---> Empty Tags");
		}else {
			LOG.debug("HIDRMS : --->  Tags not Empty");
		}
		for (Object j : tags) {
			if (((String) j).equalsIgnoreCase(tag)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkForTag(String tag, Result result) {
		String tagAction = result.getParamValueByName("tagAction");
		if (tagAction == null) {
			return false;
		}
		return tagAction.equalsIgnoreCase(tag);
	}


}
