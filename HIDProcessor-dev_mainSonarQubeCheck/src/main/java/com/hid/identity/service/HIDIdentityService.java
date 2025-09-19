package com.hid.identity.service;

import java.util.List;
import java.util.HashMap;

import com.hid.identity.customtask.FailureValidatorTaskMarker;
import com.hid.identity.customtask.IdentityTask;
import com.hid.identity.util.IdentityLogger;
import com.hid.identity.validator.AuthFactorValidator;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

@SuppressWarnings({"java:S3740", "java:S1319"})
public class HIDIdentityService implements CustomIdentityService {
    private List<IdentityTask> preValidationTasks;
    private List<IdentityTask> postValidationTasks;
    private AuthFactorValidator validator;
    private HashMap inputMap;
	private DataControllerRequest request; 
	private DataControllerResponse response;
	private Result result;
	
	public HIDIdentityService(List<IdentityTask> preValidationTasks, List<IdentityTask> postValidationTasks, AuthFactorValidator validator,HashMap inputMap, DataControllerRequest request, DataControllerResponse response,Result result) {
		 this.preValidationTasks = preValidationTasks;
		 this.postValidationTasks = postValidationTasks;
		 this.validator = validator;
		 this.inputMap = inputMap;
		 this.request = request;
		 this.response = response;
		 this.result = result;
	}
	
	@Override
	public Result doLogin() {
		for(IdentityTask task : preValidationTasks) {
			if(!task.execute(inputMap, request, response, result)) return result;
		}
		log("preValidation Tasks Done");
		
		/////Delete this code
		result.addStringParam("IdentityServicePrevalidtions", "done");
		////////
		
		boolean isSuccess = validator.validate(inputMap, request, response, result);
		if(!isSuccess) {
			log("Validator Failed");
			
			/////Delete this code
			result.addStringParam("IdentityServiceValidation", "failed");
		    ////////////

			for(IdentityTask task : postValidationTasks) {
				if(task instanceof FailureValidatorTaskMarker) {
					task.execute(inputMap, request, response, result);
				}
			}
			return result;
		}
		for(IdentityTask task : postValidationTasks) {
			if(!task.execute(inputMap, request, response, result)) return result;
		}
		log("postValidation Tasks Done");
		
	     /////Delete this code
		 result.addStringParam("IdentityServicePostValidation", "done");
	     ////////////
				
		return result;
	}
	
	private void log(String msg) {
		IdentityLogger.debug("IdentityService", this.getClass().getSimpleName(), msg);
	}

}
