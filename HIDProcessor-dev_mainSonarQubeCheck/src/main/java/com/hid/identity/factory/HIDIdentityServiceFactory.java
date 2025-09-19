package com.hid.identity.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;

import com.hid.identity.customtask.FirstFactorMFATask;
import com.hid.identity.customtask.FirstFactorParamCheckerTask;
import com.hid.identity.customtask.FirstFactorSkipMFATask;
import com.hid.identity.customtask.HIDUserAttributesTask;
import com.hid.identity.customtask.InfinityIdentityAttributesTask;
import com.hid.identity.customtask.IdentityTask;
import com.hid.identity.customtask.RMSScoreTask;
import com.hid.identity.customtask.RMSUpdateTask;
import com.hid.identity.customtask.SecondFactorParamCheckerTask;
import com.hid.identity.service.CustomIdentityService;
import com.hid.identity.service.HIDIdentityService;
import com.hid.identity.util.IdentityLogger;
import com.hid.identity.validator.ApproveValidator;
import com.hid.identity.validator.AuthFactorValidator;
import com.hid.identity.validator.NoMfaValidator;
import com.hid.identity.validator.PasswordAndOTPValidator;
import com.hid.identity.validator.StepDownValidator;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

@SuppressWarnings({"java:S1172", "java:S1126", "java:S3740", "java:S1319"})
public class HIDIdentityServiceFactory implements IdentityServiceFactory {
    private static final String MODULE_NAME_PARAM = "HIDFactory";
    private String validatorForLog = "validator is ";
    private String preTasksForLog = "PreIdentityTasks are ";
    private String postTasksForLog = "PostIdentityTasks are ";
	@Override
	public CustomIdentityService getIdentity(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result, int factor) {
		log("Inside HIDIdentityServiceFactory");
		String payload = Objects.toString(inputMap.get("payload"), "{}");
		String authType = Objects.toString(inputMap.get("authType"), "");
		String isMfa = "";
		try {
			isMfa = Objects.toString(GetConfProperties.getProperty(request, AuthenticationConstants.HID_IS_MFA_REQUIRED), "true");
		} catch (Exception e) {	
			log("Exception in fetching HID_IS_MFA_REQUIRED from server properties, hence setting it to true by default {}", e.getMessage());
		}
	    List<IdentityTask> preValidationTasks = getPreValidationTasks(isMfa, payload,factor,authType);
	    List<IdentityTask> postValidationTasks = getPostValidationTasks(isMfa, payload, factor,authType, checkForAttributes(request));
	    AuthFactorValidator validator = getValidator(authType);
	    log(preTasksForLog);
	    log(validatorForLog);
	    log(postTasksForLog);
	    ///////Delete This code
	    result.addStringParam("preTasks", preTasksForLog);
	    result.addStringParam("validator", validatorForLog);
	    result.addStringParam("postTasksForLog", postTasksForLog);
	    //////////////////
		return new HIDIdentityService(preValidationTasks, postValidationTasks, validator, inputMap, request, response, result);
	}

	private boolean checkForAttributes(DataControllerRequest request) {
		String checkForUserIdentityAttributes = "";
		try {
		   checkForUserIdentityAttributes = GetConfProperties.getProperty(request, AuthenticationConstants.HID_USER_ATR_FLAG);
		} catch (Exception e) {
			log("Exception in fetching HID_USER_ATR_FLAG from server properties, hence setting it to true by default {}", e.getMessage());
		}
		if(!checkForUserIdentityAttributes.isEmpty() && checkForUserIdentityAttributes.equalsIgnoreCase(AuthenticationConstants.USER_ATR_BYPASS)) return false;
		return true;
	}

	private List<IdentityTask> getPostValidationTasks(String isMfa, String payload, int factor, String authType, boolean isUserAttributesRequired) {
		List<IdentityTask> postValidationTasks = new ArrayList<>();
	    addRMSTasks(payload, isMfa, factor, authType, postValidationTasks);
		if(factor == 1 && "true".equalsIgnoreCase(isMfa)) {
			postTasksForLog += "|" + "FirstFactorMFATask";
			postValidationTasks.add(new FirstFactorMFATask());
			return postValidationTasks;
		}
		adduserAttributes(isMfa, factor, postValidationTasks, isUserAttributesRequired);
		return postValidationTasks;
	}

	private void adduserAttributes(String isMfa, int factor, List<IdentityTask> postValidationTasks, boolean isUserAttributesRequired) {
		IdentityTask attributeTask = isUserAttributesRequired ? new InfinityIdentityAttributesTask() : new HIDUserAttributesTask();
	    String taskName = isUserAttributesRequired ? "InfinityAttributeTask" : "HIDAttributeTask";
		if(factor == 1 && !"true".equalsIgnoreCase(isMfa)) {
			postTasksForLog += "|" + taskName;
			postValidationTasks.add(attributeTask);
			postValidationTasks.add(new FirstFactorSkipMFATask());
		}
		if(factor == 2) { 
			postTasksForLog += "|" + taskName;
			postValidationTasks.add(attributeTask);
		}
	}

	private void addRMSTasks(String payload, String isMfa, int factor, String authType, List<IdentityTask> postValidationTasks) {
		if(factor == 1 && isRMSPresent(payload)) {
			postTasksForLog += "|" + "RMSScoreTask";
			postValidationTasks.add(new RMSScoreTask(factor));

		}else if(factor == 2){
			postTasksForLog += "|" + "RMSScoreTask";
			postTasksForLog += "|" + "RMSUpdateTask";
			postValidationTasks.add(new RMSScoreTask(factor));
			postValidationTasks.add(new RMSUpdateTask());
		}
		
	}

	@SuppressWarnings({"java:S1126"})
	private boolean isRMSPresent(String payload) {
		JSONObject payloadRmsJson = new JSONObject(payload);
		JSONObject metaObj= payloadRmsJson.getJSONObject("Meta");
		if (metaObj != null && metaObj.has("rmspayload")) return true;
		return false;
	}

	@SuppressWarnings({"java:S1172"})
	private List<IdentityTask> getPreValidationTasks(String isMfa, String payload, int factor, String authType) {
		 List<IdentityTask> preValidationTasks = new ArrayList<>();
		 addValidationTasks(factor, preValidationTasks);
		 return preValidationTasks;
	}
	
	private void addValidationTasks(int factor, List<IdentityTask> preValidationTasks) {
		if(factor == 1){
			preTasksForLog += "|" + "FirstFactorParamCheckerTask" ;
			preValidationTasks.add(new FirstFactorParamCheckerTask());
		}else {
			preTasksForLog = "SecondFactorParamCheckerTask";
			preValidationTasks.add(new SecondFactorParamCheckerTask());
		}
		
	}

	private AuthFactorValidator getValidator(String authType) {
		if("APPROVE".equals(authType) || "APPROVE_DENY".equals(authType) || "APPROVE_TIMEOUT".equals(authType)) { 
		    validatorForLog += "APPROVE";
			return new ApproveValidator();
		}
		if("NO_MFA".equals(authType)) {
			validatorForLog += "NO_MFA";
			return new NoMfaValidator();
		}
		if("STEP_DOWN".equals(authType)) {
			validatorForLog += "STEP_DOWN";
			return new StepDownValidator();
		}
		validatorForLog = "PASSWORD_OR_OTP";
		return new PasswordAndOTPValidator();
	}
	
	private void log(String msg) {
		 IdentityLogger.debug(MODULE_NAME_PARAM, this.getClass().getSimpleName(), msg);
	}

}
