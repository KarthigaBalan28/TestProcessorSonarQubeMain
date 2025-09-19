package com.hid.smsdelivery.postprocessor;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hid.smsdelivery.constants.SMSConstants;
import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GenerateOTPPostprocessor implements DataPostProcessor2 {
	private static final Logger LOG = LogManager
			.getLogger(com.hid.smsdelivery.postprocessor.GenerateOTPPostprocessor.class);

	@Override
	@SuppressWarnings({"java:S1192"})
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : Inside GenerateOTPPostprocessor");
		String opstatus = result.getOpstatusParamValue();
		String errorMsg = "";
		String otp = Objects.toString(result.getParamValueByName("otp"));
		String reason = Objects.toString(result.getParamValueByName("reason"), "");
		reason = StringUtils.isEmpty(reason) ? "" : reason;
		if (StringUtils.isEmpty(opstatus) || !StringUtils.isNumeric(opstatus) || Integer.parseInt(opstatus) != 0) {
			LOG.error("HID : GenerateOTPPostprocessor --> GenerateOTP service failed");
			errorMsg = assignErrorMsg(reason);
			request.setAttribute("errorMsg", errorMsg);
			request.setAttribute("reason", reason);
			request.setAttribute("OTPServiceFailed", "true");
			LOG.error("errorMsg");
			result.addHttpStatusCodeParam(400);
			result.addOpstatusParam(-1);
			result.addStringParam("reason", reason);
			result.addStringParam("errorMsg", errorMsg);
			result.addStringParam("OTPServiceFailed", "true");
		}
		else {
			request.setAttribute("otp", otp);
		}
		return result;
	}

	private String assignErrorMsg(String reason) {
		String errorMsg = HIDFabricConstants.SERVICE_FAILURE;
		if (!reason.isEmpty()) {
			switch (reason) {
			case "50":
				return SMSConstants.OOB_GENERATION_FAILURE;

			case "6005":
				return SMSConstants.INVALID_AUTHENTICATOR;

			case "47":
				return SMSConstants.MAXIMUM_THRESHOLD;

			default:
				return errorMsg;
			}
		}
		return errorMsg;
	}
}