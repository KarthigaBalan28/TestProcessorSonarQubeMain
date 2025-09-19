package com.hid.identity.factory;

import java.util.HashMap;

import com.hid.identity.service.CustomIdentityService;
import com.hid.identity.thirdparty.DbxUserLoginIdentityService;
import com.hid.identity.util.IdentityLogger;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class ThirdPartyIdentityServiceFactory implements IdentityServiceFactory {
	


	@Override
	public CustomIdentityService getIdentity(HashMap inputMap, DataControllerRequest request,
			DataControllerResponse response, Result result, int factor) {
		log("Inside ThirdPartyIdentityServiceFactory");
		return new DbxUserLoginIdentityService(inputMap, request, response, result);
	}
	
	
	private void log(String msg) {
		 IdentityLogger.debug("Factory", this.getClass().getSimpleName(), msg);
	}

}
