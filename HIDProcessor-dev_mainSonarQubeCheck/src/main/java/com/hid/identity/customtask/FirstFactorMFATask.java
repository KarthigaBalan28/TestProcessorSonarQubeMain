package com.hid.identity.customtask;

import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.hid.identity.util.IdentityConstants;
import com.hid.identity.util.ServiceUtils;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;



public class FirstFactorMFATask implements IdentityTask {

	@SuppressWarnings({"java:S1066"})
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) {
		String authJSON = request.getAttribute(IdentityConstants.META_OBJECT_ID);
		String authId = getRandomString(10);
		JSONObject statusObj = new JSONObject();
		if (!StringUtils.isEmpty(authJSON)) {
			statusObj = new JSONObject(authJSON);
		}
		String username = Objects.toString(inputMap.get("username"), "");
		statusObj.put("username", username);
		int identityTimer = AuthenticationConstants.IDENTITY_TIMER_IN_MINTUTES * 60;
		String customTimer = "";
		try {
			customTimer = GetConfProperties.getProperty(request, AuthenticationConstants.IDENTITY_TIMER_KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!customTimer.isEmpty()) {
			if (StringUtils.isNumeric(customTimer)
					&& Integer.parseInt(customTimer) <= AuthenticationConstants.IDENTITY_TIMER_MAX_VALUE) {
				identityTimer = Integer.parseInt(customTimer) * 60;
			}
		}
		ServiceUtils.insertIntoCache(request, authId , statusObj.toString(), identityTimer);
		Record mfaMeta = getMfaMeta(result);
		mfaMeta.addStringParam("auth_id", authId);
		result.addParam(new Param("is_mfa_enabled", "true", "boolean"));
		return true;
	}
	
	
	private Record getMfaMeta(Result result) {
		Record mfaMeta = result.getRecordById(IdentityConstants.MFA_META_RECORD_ID);
		if(mfaMeta == null) {
			mfaMeta = new Record();
			mfaMeta.setId(IdentityConstants.MFA_META_RECORD_ID);
			result.addRecord(mfaMeta);
			
			////Delete this Code
			result.addStringParam("MFAMETA", "Done");
			//////
		}
		return mfaMeta;
	}


	private String getRandomString(int count) {
		String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int) (Math.random() * (alphaNumericString.length() - 1)); //NOSONAR
			builder.append(alphaNumericString.charAt(character));
		}
		return builder.toString();
	}

}
