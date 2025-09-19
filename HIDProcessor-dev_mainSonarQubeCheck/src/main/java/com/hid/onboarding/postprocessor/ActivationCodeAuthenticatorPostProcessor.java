package com.hid.onboarding.postprocessor;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class ActivationCodeAuthenticatorPostProcessor implements DataPostProcessor2 {
	
	private static final Logger LOG = LogManager.getLogger(com.hid.onboarding.postprocessor.ActivationCodeAuthenticatorPostProcessor.class);
	private static final String SEQUENCE_FAILED_PARAM = "sequenceFailed";
	private static final String ERROR_MSG_DETAIL_PARAM = "errorMsgDetail";

	@SuppressWarnings("java:S2629")
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : In ActivationCodeAuthenticatorPostProcessor");
		String opstatus = result.getParamValueByName("opstatus");
		int os = Integer.parseInt(opstatus);
		if( os!=0) {
			LOG.debug("HID : opstatus is {}. Not processing further.", String.valueOf(os));
			String errMsg = result.getParamValueByName("ActivationCodeError");
			errMsg = StringUtils.isEmpty(errMsg) ? HIDFabricConstants.SERVICE_FAILURE: errMsg;
			request.setAttribute(SEQUENCE_FAILED_PARAM, true);
			request.setAttribute(ERROR_MSG_DETAIL_PARAM, errMsg);
			return result;
		}
		String active = result.getParamValueByName("activationAuthStatus");
		String consecutiveFailed = result.getParamValueByName("consecutiveFailed");
		String consecutiveSuccess = result.getParamValueByName("consecutiveSuccess");
		LOG.debug("HID : Result values, active : {}, consecutiveFailed : {}, consecutiveSuccess : {}", active, consecutiveFailed, consecutiveSuccess);
		if(!StringUtils.isEmpty(active) && active.equals("false")) {
			 result.addOpstatusParam(-1);
			 result.addErrMsgParam(HIDFabricConstants.EXPIRED);
			 LOG.debug("HID : Activation code expired");
		     request.setAttribute(SEQUENCE_FAILED_PARAM, true);
			 request.setAttribute(ERROR_MSG_DETAIL_PARAM, HIDFabricConstants.EXPIRED);
		}
		if(!StringUtils.isEmpty(consecutiveFailed)) {
			int cf = Integer.parseInt(consecutiveFailed);
			if(cf >= 3) {
				result.addOpstatusParam(-1);
				result.addErrMsgParam(HIDFabricConstants.THRESHOLD);
				request.setAttribute(SEQUENCE_FAILED_PARAM, true);
				request.setAttribute(ERROR_MSG_DETAIL_PARAM, HIDFabricConstants.THRESHOLD);
				LOG.debug("HID : Activation code threshold reached");
			}
		}
		if(!StringUtils.isEmpty(consecutiveSuccess)) {
			int cs = Integer.parseInt(consecutiveSuccess);
			if(cs > 0) {
				result.addOpstatusParam(-1);
				result.addErrMsgParam(HIDFabricConstants.AUTH_USED);
				LOG.debug("HID : Activation code already consumed");
				request.setAttribute(SEQUENCE_FAILED_PARAM, true);
				request.setAttribute(ERROR_MSG_DETAIL_PARAM, HIDFabricConstants.AUTH_USED);
			}
		}
		return result;
	}

}
