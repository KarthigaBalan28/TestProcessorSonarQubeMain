package com.hid.identity.customtask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import org.json.JSONObject;

import com.hid.identity.util.IdentityLogger;
import com.hid.identity.util.ServiceUtils;
import com.hid.util.AuthenticationConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class FirstFactorParamCheckerTask implements IdentityTask {

    private HashSet<String> authSet;
    
    private static final String PASSWORD_PARAM = "password";
    private static final String CORRELATION_ID_PARAM = "correlationId";
    private static final String AUTH_TYPE_PARAM = "authType";
    
    @SuppressWarnings("java:S3012")
	public FirstFactorParamCheckerTask(){
		authSet = new HashSet<>();
		String authString = AuthenticationConstants.SUPPORTED_FIRST_FACTORS;
		for(String s : authString.split(",")) {
			authSet.add(s);
		}
	}
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) {
		        String username = "";
		        String password = "";
		        String requestUri = "";
		        String csrf = "";
		        String correlationId = "";
		        JSONObject passwordObject = null;
		        String authType = Objects.toString(inputMap.get(AUTH_TYPE_PARAM), "$$$");
				try {
					String payload = Objects.toString(inputMap.get("payload"), null);
					JSONObject payloadJson = new JSONObject(payload);
					String eventKey = payloadJson.getJSONObject("Meta").getString("EventType");
					JSONObject loginJson = payloadJson.getJSONObject(eventKey);
					if(authType.equalsIgnoreCase("FIDO")) {
						username = loginJson.optString("userid","");
						requestUri = loginJson.optString("request_uri","");
						csrf = loginJson.optString("csrf","");
						passwordObject = loginJson.getJSONObject(PASSWORD_PARAM);
						correlationId = loginJson.optString(CORRELATION_ID_PARAM, "");
					} else {
						username = loginJson.optString("userid","");
						password = loginJson.optString(PASSWORD_PARAM,"");
						correlationId = loginJson.optString(CORRELATION_ID_PARAM, "");
					}
					
					
					
				} catch (Exception e) {
					ServiceUtils.setErrorToResult(result,"exception while invoking service Invalid Payload JSON" + e.getMessage(), -1, 401);
					return false;					
				}
				if((username.isEmpty() || password.isEmpty()) && ((!authType.equals("APPROVE")) 
						&& (!authType.equals("FIDO")))) {
					
					ServiceUtils.setErrorToResult(result, AuthenticationConstants.EMPTY_UN_PWD, -1, 401);
					return false;
				}
				if(authType.equals("APPROVE") && password.isEmpty()) {
					ServiceUtils.setErrorToResult(result, AuthenticationConstants.EMPTY_UN_PWD, -1, 401);
					return false;
				}
				if(!authSet.contains(authType)) {
					log(authType + " is not supported Currentlty");
					ServiceUtils.setErrorToResult(result, authType + " Invalid Authenticator", -1, 400);
					return false;
				}
				if(passwordObject != null) {
					log("password for FIDO: "+ Objects.toString(passwordObject));
				}
			
				if(authType.equalsIgnoreCase("FIDO")) {
					inputMap.put("username", username);
					inputMap.put(PASSWORD_PARAM, passwordObject.toString());
					inputMap.put(AUTH_TYPE_PARAM, authType);
					inputMap.put("request_uri",requestUri);
					inputMap.put("csrf",csrf);
					inputMap.put(CORRELATION_ID_PARAM, correlationId);
					log("inputMap for FIDO: " +inputMap.toString());
				} else {
					inputMap.put("username", username);
					inputMap.put(PASSWORD_PARAM, password);
					inputMap.put(AUTH_TYPE_PARAM, authType);
					inputMap.put(CORRELATION_ID_PARAM, correlationId);
				}				
				result.addStringParam("userNameFromValidatorTask", username);
				return true;
	}
	
	private void log(String msg) {
		 IdentityLogger.debug("PreValidation Task", this.getClass().getSimpleName(), msg);
	}

}
