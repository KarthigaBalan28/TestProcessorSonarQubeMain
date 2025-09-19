package com.hid.onboarding.postprocessor;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class ActivationCodeLoginPostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager.getLogger(com.hid.onboarding.postprocessor.ActivationCodeLoginPostProcessor.class);
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : In ActivationCodeLoginPostProcessor");
		String idToken = result.getParamValueByName("id_token");
		if(!StringUtils.isEmpty(idToken)) {
			request.setAttribute("sequenceFailed", false);
			result.getParamByName("id_token").setValue("");
			result.addStringParam("validationStatus", "success");
		}else {
	    	String servErrMsg = result.getParamValueByName("ServErrMsg");
			LOG.debug("HID : Server Error Message {}", servErrMsg);
			result.addStringParam("validationStatus", "failure");
			request.setAttribute("sequenceFailed", true);
			request.setAttribute("errorMsgDetail", HIDFabricConstants.INCORRECT_ACTIVATION_CODE);
		}
		return result;
	}

}
