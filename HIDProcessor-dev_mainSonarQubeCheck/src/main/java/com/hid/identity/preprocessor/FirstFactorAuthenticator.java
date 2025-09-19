package com.hid.identity.preprocessor;

import java.util.HashMap;

import com.hid.common.ClientBasePreprocessor;
import com.hid.identity.factory.IdentityServiceFactory;
import com.hid.identity.service.CustomIdentityService;
import com.hid.identity.service.ServiceManager;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class FirstFactorAuthenticator extends ClientBasePreprocessor implements DataPreProcessor2 {

	@SuppressWarnings({"java:S1854"})
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		IdentityServiceFactory factory = ServiceManager.getIdentityFactory(inputMap, request);
		CustomIdentityService identityService = factory.getIdentity(inputMap, request, response, result, 1);
		result = identityService.doLogin();
		return false;
	}

}
