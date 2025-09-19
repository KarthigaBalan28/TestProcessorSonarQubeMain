package com.hid.identity.thirdparty;

import java.util.HashMap;

import com.hid.identity.service.CustomIdentityService;
import com.hid.identity.util.IdentityLogger;
import com.hid.util.HIDIntegrationServices;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

@SuppressWarnings({"java:S3740" , "java:S1068" , "java:S1319"})
public class DbxUserLoginIdentityService implements CustomIdentityService{
	private HashMap inputMap;
	private DataControllerRequest request; 
	private DataControllerResponse response;
	private Result result;
	
	
	
	public  DbxUserLoginIdentityService(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) {
		this.inputMap = inputMap;
		this.request = request;
		this.response = response;
		this.result = result;
	}



	@Override
	public Result doLogin() {
		ThirdPartyServiceMeta serviceMeta = new DBXUserIdentityMetaDataClass();
		Result loginResult = null;
		try {
			loginResult = HIDIntegrationServices.call(
						serviceMeta.getServiceName(), 
						serviceMeta.getOperationName(), 
						request, 
						serviceMeta.formHeaderMap(inputMap, request), 
						serviceMeta.formRequestMap(inputMap, request)
					);
			log("is_mfa_enabled " + loginResult.getParamValueByName("is_mfa_enabled"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(loginResult != null) loginResult.addParam(new Param("is_mfa_enabled", "false", "boolean"));
		return loginResult;
	}
	
	
	private void log(String msg) {
		 IdentityLogger.debug("ThirdPartyService", this.getClass().getSimpleName(), msg);
	}

      
}
