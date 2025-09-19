package com.hid.usermanagement.postprocessor;

import org.apache.logging.log4j.Logger; 
import org.apache.logging.log4j.LogManager;

import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
@SuppressWarnings({"java:S2629"})
public class ChangePasswordPostProcessor implements DataPostProcessor2 {
	private static final Logger LOG = LogManager
			.getLogger(com.hid.usermanagement.postprocessor.ChangePasswordPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : In ChangePasswordPostProcessor");		
		String error = result.getParamValueByName("ChangePasswordError") == null ? "" : result.getParamValueByName("ChangePasswordError");	
		LOG.debug("Error message : {}", error);
		String opstatus = result.getParamValueByName("opstatus");
		int os = Integer.parseInt(opstatus);
		if (os != 0 || !error.isEmpty()) {
			LOG.debug("HID : opstatus is {}. Not processing further.", String.valueOf(os));
			error = error.isEmpty() ? HIDFabricConstants.SERVICE_FAILURE : error;
			request.setAttribute("sequenceFailed", true);
			request.setAttribute("errorMsgDetail", error);
			result.getParamByName("status").setValue("false");
		} else {
			LOG.debug("HID : No error message, sending status true");
			result.getParamByName("status").setValue("true");
		}
		return result;
	}

}
