package com.hid.identity.endpoints;

import java.util.HashMap;

import com.hid.identity.factory.IdentityServiceFactory;
import com.hid.identity.service.CustomIdentityService;
import com.hid.identity.service.ServiceManager;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class SecondFactorEndpoint implements JavaService2 {

	@SuppressWarnings({"java:S3740"})
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		HashMap inputMap = (HashMap<?,?>) inputArray[1];
		IdentityServiceFactory factory = ServiceManager.getIdentityFactory(inputMap, request);
		CustomIdentityService identityService = factory.getIdentity(inputMap, request, response, new Result(), 2);
		return identityService.doLogin();
	}

}
