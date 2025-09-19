package com.hid.identity.validator;

import java.util.HashMap;
import java.util.Objects;

import com.hid.identity.util.IdentityLogger;
import com.hid.identity.util.ServiceUtils;
import com.hid.util.AuthenticationConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.registry.AppRegistryException;

@SuppressWarnings({"java:S3740" , "java:S1854" , "java:S1481", "java:S1172" , "java:S1319"})
public class ApproveValidator implements AuthFactorValidator {

	private	static final String VALIDATOR_STATUS = "validatorStatus";
	private static final String APPROVE_PARAM = "APPROVE";

	@Override
	public boolean validate(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) {
		  String authType = Objects.toString(inputMap.get("authType"), APPROVE_PARAM);
		  if(APPROVE_PARAM.equals(authType)) {
			  return approveValidator(inputMap, request, response, result);
		  }
		  return nonApproveValidator(inputMap, request, response, result);
		
	}
	
	private boolean approveValidator(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) {
		String authType = APPROVE_PARAM;
		String username = Objects.toString(inputMap.get("username"), "");
		String password = Objects.toString(inputMap.get("password"), "");
		String authString = "";
		try {
			authString = Objects.toString(request.getServicesManager().getResultCache().retrieveFromCache(password),
					null);
		} catch (AppRegistryException e) {
			log("AppRegistryException " + e.getMessage());
			e.printStackTrace();
			ServiceUtils.setErrorToResult(result, "AppRegistryException " + e.getMessage(), -1, 401);
			return false;
		}
		if (authString.isEmpty()) {
			request.setAttribute(VALIDATOR_STATUS, "failure");
			ServiceUtils.setErrorToResult(result, AuthenticationConstants.APPROVE_STATUS_NOT_KNOWN, -1, 401);
			return false;
		}
		result.addOpstatusParam(0);
		result.addHttpStatusCodeParam(200);
		request.setAttribute(VALIDATOR_STATUS, "success");
		ServiceUtils.removeFromCache(request, password);
		return true;
	}
	
	private boolean nonApproveValidator(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result){
		ServiceUtils.setErrorToResult(result, "Approve declined by user /timed out", -1, 401);
		request.setAttribute(VALIDATOR_STATUS, "failure");
		return false;
	}
	
	
	private void log(String msg) {
		 IdentityLogger.debug("Validator", this.getClass().getSimpleName(), msg);
	}

	

}
