package com.hid.identity.endpoints;

import java.util.HashMap;

import com.hid.identity.factory.IdentityServiceFactory;
import com.hid.identity.service.CustomIdentityService;
import com.hid.identity.service.ServiceManager;
import com.hid.identity.util.IdentityLogger;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

@SuppressWarnings({"java:S3740", "java:S1319", "java:S1144"})
public class FirstFactorEndPoint implements JavaService2 {

	@Override
	public Result invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		HashMap inputMap = (HashMap) inputArray[1];
		IdentityServiceFactory factory = ServiceManager.getIdentityFactory(inputMap, request);
		CustomIdentityService identityService = factory.getIdentity(inputMap, request, response, new Result(), 1);
		return identityService.doLogin();
	}
	
	private void log(String msg) {
		 IdentityLogger.debug("Endpoint", this.getClass().getSimpleName(), msg);
	}

}
