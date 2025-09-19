package com.hid.authentication.postprocessor;

import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class ValidateMfaOrchPostProcessor implements DataPostProcessor2 {
	private static final Logger LOG = LogManager
			.getLogger(com.hid.authentication.postprocessor.ValidateMfaOrchPostProcessor.class);

	@SuppressWarnings("java:S1125")
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		
		LOG.debug("HID::In ValidateMfaOrchPostProcessor");
		boolean sequenceFailed = request.getAttribute("sequenceFailed") == null ? false : request.getAttribute("sequenceFailed");
		LOG.debug("HID::ValidateMfaOrchPostProcessor : Value of sequence failed is : {}", sequenceFailed);
		if(sequenceFailed) {
			String errMessage = request.getAttribute("errMessage") == null ? HIDFabricConstants.SERVICE_FAILURE : request.getAttribute("errMessage");
			LOG.debug("HID::ValidateMfaOrchPostProcessor, value of custom error message is : {}", errMessage);			
			result.addErrMsgParam(errMessage);
			result.addOpstatusParam(-1);			
		}		
		return result;
	}
}
