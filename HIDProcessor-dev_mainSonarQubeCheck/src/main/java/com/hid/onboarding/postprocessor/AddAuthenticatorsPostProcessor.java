package com.hid.onboarding.postprocessor;

import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class AddAuthenticatorsPostProcessor implements DataPostProcessor2 {
	private static final Logger LOG = LogManager.getLogger(com.hid.onboarding.postprocessor.AddAuthenticatorsPostProcessor.class);

	@SuppressWarnings("java:S1125")
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : In AddAuthenticatorsPostProcessor");
		boolean isPasswordAdded = request.getAttribute("isPasswordAdded") == null ? true : request.getAttribute("isPasswordAdded");
		LOG.debug("HID : Value of isPasswordAdded {}", isPasswordAdded);
		if(!isPasswordAdded) {
			LOG.debug("HID : Static password authenticator already exists");
	        result.addOpstatusParam(-1);
	    	result.addErrMsgParam(HIDFabricConstants.PWD_EXISTS);
		}
		 return result;
		
	}

}
