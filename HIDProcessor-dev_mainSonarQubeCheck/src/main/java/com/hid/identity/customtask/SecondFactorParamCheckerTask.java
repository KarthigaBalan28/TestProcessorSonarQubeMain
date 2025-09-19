package com.hid.identity.customtask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.hid.identity.util.IdentityConstants;
import com.hid.identity.util.IdentityLogger;
import com.hid.identity.util.ServiceUtils;
import com.hid.util.AuthenticationConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.registry.AppRegistryException;

public class SecondFactorParamCheckerTask implements IdentityTask {
	  private HashSet<String> authSet;
	  public SecondFactorParamCheckerTask(){
			authSet = new HashSet<>();
			String authString = AuthenticationConstants.SUPPORTED_SECOND_FACTORS;
			for(String s : authString.split(",")) {
				authSet.add(s);  //NOSONAR
			}
		}
	
	@SuppressWarnings({"java:S106"})
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) {
		String mfaKey = Objects.toString(inputMap.get("mfa_key"), null);
		String password = Objects.toString(inputMap.get("password"), "");
		String authType = Objects.toString(inputMap.get("authType"), "$$$");
		String correlationId = Objects.toString(inputMap.get("correlationId"), "");
		String authJsonString = "";
		try {
			authJsonString = Objects
					.toString(request.getServicesManager().getResultCache().retrieveFromCache(mfaKey), null);
		}catch(AppRegistryException e) {
			log("AppRegistryException occured while calling SecondFactorParamCheckerTask  "
					+ e.getMessage());
			ServiceUtils.setErrorToResult(result, "Exception occured while calling CustomValidation Service "
					+ e.getMessage(), -2, 401);
			e.printStackTrace();
		    return false;
		}
		if (StringUtils.isEmpty(authJsonString)) {
			log("HID::CustomMFAValidation ---> mfa_key not found in cache");
			ServiceUtils.setErrorToResult(result, AuthenticationConstants.FIRST_FACTOR_NOT_AUTHENTICATED, -1, 401);
			return false;
		}
		if(!authSet.contains(authType)) {
			log(authType + " is not supported Currentlty");
			ServiceUtils.setErrorToResult(result, authType + " Invalid Authenticator", -1, 400);
			return false;
		}
		System.out.println("authJsonString "+ authJsonString);
		JSONObject authJsonObj = new JSONObject(authJsonString);
		String username = authJsonObj.optString("username", "");
		
		inputMap.put("username", username);
		inputMap.put("password", password);
		inputMap.put("authType", authType);
		inputMap.put("mfa_key", mfaKey);
		inputMap.put("correlationId",correlationId);
		
	    request.setAttribute(IdentityConstants.META_OBJECT_ID, authJsonString);
		return true;
	}
	
	private void log(String msg) {
		 IdentityLogger.debug("Factory", this.getClass().getSimpleName(), msg);
	}

}
