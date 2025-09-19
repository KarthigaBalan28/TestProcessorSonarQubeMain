package com.hid.onboarding.postprocessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class PARPostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager.getLogger(com.hid.onboarding.postprocessor.PARPostProcessor.class);

	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception
	{
		LOG.debug("HID : In PARPostProcessor");
		String requestURI = result.getParamValueByName("request_uri") == null ? "" : result.getParamValueByName("request_uri");
		
		if(!requestURI.isEmpty())
		{
			request.setAttribute("requestURI", requestURI);
			LOG.debug("HID : request_uri received, setting value for request");
		}
		else
		{
			result.addOpstatusParam(-1);
			request.setAttribute("sequenceFailed", true);
			request.setAttribute("errorMsgDetail", HIDFabricConstants.SERVICE_FAILURE);
		}
		
		return result;
	}
}
