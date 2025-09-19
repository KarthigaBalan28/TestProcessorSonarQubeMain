package com.hid.onboarding.postprocessor;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class SendOOBPostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager.getLogger(com.hid.onboarding.postprocessor.SendOOBPostProcessor.class);
	
	private static final String SEQUENCE_FAILED_PARAM = "sequenceFailed";
	private static final String ERROR_MSG_DETAIL_PARAM = "errorMsgDetail";

	@SuppressWarnings("java:S1125")
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : In SendOOBPostProcessor");

		boolean sequenceFailed = request.getAttribute(SEQUENCE_FAILED_PARAM) == null ? false
				: request.getAttribute(SEQUENCE_FAILED_PARAM);
		if (sequenceFailed) {
			String errorMsgDetail = request.getAttribute(ERROR_MSG_DETAIL_PARAM) == null ? HIDFabricConstants.AUTH_TX_OOB_FAILURE
					: request.getAttribute(ERROR_MSG_DETAIL_PARAM);
			request.setAttribute(ERROR_MSG_DETAIL_PARAM, errorMsgDetail);
			request.setAttribute(SEQUENCE_FAILED_PARAM, true);
			return result;
		}

		String otpStatus = Objects.toString(result.getParamValueByName("OOB_SENT"), "");
		String errorDescription = Objects.toString(result.getParamValueByName("error_description"), "");
		String reason = Objects.toString(result.getParamValueByName("reason"), "");
		if ("".equals(otpStatus)) {
			request.setAttribute(SEQUENCE_FAILED_PARAM, true);
			request.setAttribute(ERROR_MSG_DETAIL_PARAM, HIDFabricConstants.AUTH_SEND_OTP_FAILURE);
			request.setAttribute("error_description", errorDescription);
			request.setAttribute("reason", reason);
		}
		return result;
	}

}