package com.hid.identity.validator;

import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.hid.dataclasses.HIDIntServiceDataclass;
import com.hid.identity.util.IdentityLogger;
import com.hid.identity.util.ServiceUtils;
import com.hid.util.HIDIntegrationServices;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class PasswordAndOTPValidator implements AuthFactorValidator {

	private static final String AUTH_TYPE_PARAM = "authType";
	private static final String USERNAME_PARAM = "username";
	private static final String PASSWORD_PARAM = "password";
	private static final String AUTHORIZATION_PARAM = "Authorization";
	private static final String CORRELATION_ID_PARAM = "correlationId";

	@Override
	public boolean validate(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) {
	    String authType = Objects.toString(inputMap.get(AUTH_TYPE_PARAM));
		HIDIntServiceDataclass serviceData = HIDIntegrationServices.getHIDServiceDataObject(authType);
		String serviceName = "";
		String operationName = "";
		if (authType.equalsIgnoreCase("FIDO")) {
		      serviceName = "HIDFIDOOrch";
		      operationName = "authenticate";
		    } else {
		      serviceName = serviceData.getServiceName();
		      operationName = serviceData.getOperationName();
		    } 
		log("Service Call is " + serviceName + "." + operationName);
	    result.addStringParam("ServiceCallInValidator", serviceName + "." + operationName);
		Result result1 = null;
		try {
			if(authType.equalsIgnoreCase("FIDO")) {
				result1 = HIDIntegrationServices.call(serviceName, operationName, request, 
						getHeadersMapFido(inputMap,request), getBodyMapFido(inputMap,request));
			} else {
				result1 = HIDIntegrationServices.call(serviceName, operationName, request, 
						getHeadersMap(inputMap,request), getBodyMap(inputMap,request));
			}		   
		} catch (Exception e) {
			String errorMsg = "Exception while invoking the service " + serviceName + "." + operationName + " with message " + e.getMessage();
		    log(errorMsg);
			e.printStackTrace();
			ServiceUtils.setErrorToResult(result, errorMsg, -1, 401);
			return false;
		}
		return validateResult(result1,result,request);
	}

	@SuppressWarnings({"java:S1854"})
	private boolean validateResult(Result currResult, Result mainResult, DataControllerRequest request) {
		
		log("In ValidateResult currResult: "+currResult.getAllParams());
		
		String accessToken = currResult.getParamValueByName("access_token");
		String idToken = currResult.getParamValueByName("id_token");
		idToken = StringUtils.isEmpty(idToken) ? accessToken : idToken;
		if (!StringUtils.isEmpty(accessToken)) {
			mainResult.addOpstatusParam(0);
			mainResult.addHttpStatusCodeParam(200);
			request.setAttribute("validatorStatus", "success");
			return true;
		}else {
			String errorMsg = currResult.getParamValueByName("errormsg");
			String reason = Objects.toString(currResult.getParamValueByName("reason"));
			request.setAttribute("validatorStatus", "failure");
			reason = StringUtils.isEmpty(reason) ? "" : reason;
			errorMsg = StringUtils.isEmpty(errorMsg) ? "Invalid user credentials " : errorMsg+" REASON-"+reason;
		    log("Validator Failure with errorMsg " + errorMsg);
		    ServiceUtils.setErrorToResult(mainResult, errorMsg, -1, 401);
		}
		return false;
	}

	@SuppressWarnings({"java:S3740"})
	private HashMap<String, Object> getBodyMap(HashMap inputMap, DataControllerRequest request) {
		HashMap<String, Object> bodyMap = new HashMap<>();
		bodyMap.put(USERNAME_PARAM, inputMap.get(USERNAME_PARAM));
		bodyMap.put(PASSWORD_PARAM, inputMap.get(PASSWORD_PARAM));
		bodyMap.put(AUTH_TYPE_PARAM, inputMap.get(AUTH_TYPE_PARAM));
		return bodyMap;
	}
	
	@SuppressWarnings({"java:S3740"})
	private HashMap<String, Object> getBodyMapFido(HashMap inputMap, DataControllerRequest request) {
		HashMap<String, Object> bodyMap = new HashMap<>();
		bodyMap.put(USERNAME_PARAM, inputMap.get(USERNAME_PARAM));
		bodyMap.put(PASSWORD_PARAM, inputMap.get(PASSWORD_PARAM));
		bodyMap.put(AUTH_TYPE_PARAM, inputMap.get(AUTH_TYPE_PARAM));
		bodyMap.put("request_uri", inputMap.get("request_uri"));
		String correlationId = inputMap.get(CORRELATION_ID_PARAM) == null ? "" : inputMap.get(CORRELATION_ID_PARAM).toString();
		bodyMap.put(CORRELATION_ID_PARAM, correlationId);
		return bodyMap;
	}

	@SuppressWarnings({"java:S3740"})
	private HashMap<String, Object> getHeadersMap(HashMap inputMap, DataControllerRequest request) {
		HashMap<String, Object> headerMap = new HashMap<>();
		String bearerToken = request.getParameter(AUTHORIZATION_PARAM) == null ? "" : request.getParameter(AUTHORIZATION_PARAM);
		headerMap.put(AUTHORIZATION_PARAM, bearerToken);
		String correlationId = inputMap.get(CORRELATION_ID_PARAM) == null ? "" : inputMap.get(CORRELATION_ID_PARAM).toString();
		headerMap.put("X-Correlation-ID", correlationId);
		return headerMap;
	}
	
	@SuppressWarnings({"java:S3740"})
	private HashMap<String, Object> getHeadersMapFido(HashMap inputMap, DataControllerRequest request) {
		HashMap<String, Object> headerMap = new HashMap<>();
		String bearerToken = request.getParameter(AUTHORIZATION_PARAM) == null ? "" : request.getParameter(AUTHORIZATION_PARAM);
		String csrfToken = inputMap.get("csrf") == null ? "" : inputMap.get("csrf").toString();
		headerMap.put(AUTHORIZATION_PARAM, bearerToken);
		headerMap.put("server-csrf-token", csrfToken);
		String correlationId = inputMap.get(CORRELATION_ID_PARAM) == null ? "" : inputMap.get(CORRELATION_ID_PARAM).toString();
		headerMap.put("X-Correlation-ID", correlationId);
		return headerMap;
	}

	private void log(String msg) {
		 IdentityLogger.debug("Validator", this.getClass().getSimpleName(), msg);
	}
	
}
