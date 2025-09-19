package com.hid.onboarding.postprocessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class RegisterCredentialsPostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.postprocessor.RegisterCredentialsPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {

		LOG.debug("HID : In RegisterCredentialsPostProcessor");
		Result res = new Result();
		String errmsg = result.getParamValueByName("errmsg")== null? "" : result.getParamValueByName("errmsg");
		
		if(!errmsg.isEmpty() && errmsg.equalsIgnoreCase("empty response received")) {
			res.addOpstatusParam(0);
			res.addHttpStatusCodeParam(201);
			res.addParam("success", "true");
			return res;
		}
		return result;
	}
}
