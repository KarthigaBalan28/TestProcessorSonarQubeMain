package com.hid.onboarding.postprocessor;

import org.apache.logging.log4j.Logger; 
import org.apache.logging.log4j.LogManager;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class AssociateHWDevicePostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.postprocessor.AssociateHWDevicePostProcessor.class);
	
	private static final String SEQUENCE_FAILED_PARAM = "sequenceFailed";

	@SuppressWarnings({"java:S5411","java:S1125"})
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : In AssociateHWDevicePostProcessor");
		
		Boolean sequenceFailed = request.getAttribute(SEQUENCE_FAILED_PARAM) == null ? false : request.getAttribute(SEQUENCE_FAILED_PARAM);

		if(sequenceFailed) {
			result.addOpstatusParam(-1);
			result.addStringParam("ServiceFailed", request.getAttribute("errorMsgDetail"));
			return result;
		}
		
		request.setAttribute(SEQUENCE_FAILED_PARAM, false);
		
		return result;
	}
}
