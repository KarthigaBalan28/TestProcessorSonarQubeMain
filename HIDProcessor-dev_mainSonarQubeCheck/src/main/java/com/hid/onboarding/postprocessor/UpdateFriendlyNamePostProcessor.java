package com.hid.onboarding.postprocessor;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class UpdateFriendlyNamePostProcessor implements DataPostProcessor2{

	public static final Logger LOG = LogManager.getLogger(UpdateFriendlyNamePostProcessor.class);
	
	@Override
	public Object execute(Result result , DataControllerRequest request , DataControllerResponse response) throws Exception {
		
		String authKey = Objects.toString(request.getAttribute("auth_key"),"");
		
		request.getServicesManager().getResultCache().removeFromCache(authKey);
		
		return result;
		
	}
}
