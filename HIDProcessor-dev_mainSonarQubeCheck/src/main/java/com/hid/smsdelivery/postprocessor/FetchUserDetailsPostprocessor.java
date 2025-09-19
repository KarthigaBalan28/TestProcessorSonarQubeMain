package com.hid.smsdelivery.postprocessor;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hid.smsdelivery.constants.SMSConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class FetchUserDetailsPostprocessor implements DataPostProcessor2 {
	private static final Logger LOG = LogManager
			.getLogger(com.hid.smsdelivery.postprocessor.FetchUserDetailsPostprocessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : Inside FetchUserDetailsPostprocessor");
		String opstatus = result.getOpstatusParamValue();
		String errorMsg = "";
		String phoneNo = Objects.toString(result.getParamValueByName("phoneNo"));
		String reason = Objects.toString(result.getParamValueByName("reason"), "");
		reason = StringUtils.isEmpty(reason) ? "" : reason;
		if (StringUtils.isEmpty(opstatus) || !StringUtils.isNumeric(opstatus) || Integer.parseInt(opstatus) != 0) {
			LOG.error("HID : FetchUserDetails service failed");
			errorMsg = SMSConstants.SEARCH_USER_FAILED;
			request.setAttribute("searchUserErrorMsg", errorMsg);
			request.setAttribute("reason", reason);
			request.setAttribute("searchUserFailed", "true");
			result.addHttpStatusCodeParam(400);
			result.addOpstatusParam(-1);
		}
		else {
			request.setAttribute("phoneNo", phoneNo);
		}
		return result;
	}
}
