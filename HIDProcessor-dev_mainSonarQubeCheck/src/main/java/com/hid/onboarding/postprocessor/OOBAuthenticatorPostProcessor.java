package com.hid.onboarding.postprocessor;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hid.util.AuthenticationConstants;
import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class OOBAuthenticatorPostProcessor implements DataPostProcessor2{

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.postprocessor.OOBAuthenticatorPostProcessor.class);
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		
		LOG.debug("HID : Inside OOBAuthenticatorPostProcessor");
		Result res = new Result();
		String transactionId = Objects.toString(request.getAttribute("transactionId"), "");
		if (transactionId != null && !transactionId.isEmpty()) {
			request.getServicesManager().getResultCache().removeFromCache(transactionId);
			LOG.debug("transactionId is successFully removed from cache");
		} else {
			LOG.debug("HID Cache has been removed");
			res.addOpstatusParam(-1);
			res.addStringParam("sendOOBServiceError", AuthenticationConstants.INVALID_AUTH_KEY);
			res.addErrMsgParam("Invalid request payload");
			res.addHttpStatusCodeParam(400);
			return res;
		}
		String otpStatus = Objects.toString(result.getParamValueByName("OOB_SENT"), ""); 
		String errorDescription = Objects.toString(result.getParamValueByName("error_description"), "");
		String reason = Objects.toString(result.getParamValueByName("reason"), "");
		LOG.debug("HID : otpStatus {}", otpStatus);
		if ("".equals(otpStatus)) {
			LOG.debug("HID: otpSent failed");
			res.addHttpStatusCodeParam(401);
			res.addErrMsgParam("Failed to send the OTP");
			res.addStringParam("sendOOBServiceError",HIDFabricConstants.AUTH_SEND_OTP_FAILURE);
			res.addStringParam("error_description", errorDescription);
			res.addStringParam("reason", reason);
			return res;
		}
		return result;
	}

}
