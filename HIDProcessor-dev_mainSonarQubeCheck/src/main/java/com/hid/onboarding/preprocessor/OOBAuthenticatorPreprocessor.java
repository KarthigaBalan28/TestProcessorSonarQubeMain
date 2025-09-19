package com.hid.onboarding.preprocessor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.AuthenticationConstants;
import com.hid.util.HIDFabricConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class OOBAuthenticatorPreprocessor extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.preprocessor.OOBAuthenticatorPreprocessor.class);
	
	private static final String SEQUENCE_FAILED_PARAM = "sequenceFailed";
	private static final String ERROR_MSG_DETAIL_PARAM = "errorMsgDetail";

	@SuppressWarnings({"java:S1125","java:S1185","java:S3776"})
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if (super.execute(inputMap, request, response, result)) {
			LOG.debug("HID : In OOBAuthenticatorPreprocessor");
			boolean sequenceFailed = request.getAttribute(SEQUENCE_FAILED_PARAM) == null ? false
					: request.getAttribute(SEQUENCE_FAILED_PARAM);
			
			if (sequenceFailed) {
				String errorMsgDetail = request.getAttribute(ERROR_MSG_DETAIL_PARAM) == null ? HIDFabricConstants.AUTH_TX_OOB_FAILURE
						: request.getAttribute(ERROR_MSG_DETAIL_PARAM);
				result.addOpstatusParam(-1);
				request.setAttribute(ERROR_MSG_DETAIL_PARAM, errorMsgDetail);
				request.setAttribute(SEQUENCE_FAILED_PARAM, true);
				return false;
			}
			
			String correlationId = Objects.toString(inputMap.get("correlationId"), "");
			request.addRequestParam_("X-Correlation-ID", correlationId);
			
			String oob = Objects.toString(inputMap.get("AuthenticationType"), "");
			String authenticatorType = oob.equals("AT_OOBSMS") ? AuthenticationConstants.HID_OTP_SMS_ENV_VARIABLE_KEY : AuthenticationConstants.HID_OTP_EML_ENV_VARIABLE_KEY;
			LOG.debug("HID : Getting value of {} from server settings", authenticatorType);
			String authType = GetConfProperties.getProperty(request, authenticatorType);
			LOG.debug("HID : Value of {} from server settings is : {}", authenticatorType, authType);
			if (!authType.isEmpty()) {
				LOG.debug("HID : Setting the value of AuthenticatorType in input parameter");
				inputMap.put("AuthenticationType", authType);
			}
			String isPasswordRequired = Objects.toString(inputMap.get("isPasswordRequired"), "false");
			if("false".equals(isPasswordRequired)) {
				inputMap.put("password", "null");
			}
			String channelId = GetConfProperties.getProperty(request,
					AuthenticationConstants.HID_IDP_CHANNEL_ENV_VARIABLE_KEY);
			if (!StringUtils.isEmpty(channelId)) {
				inputMap.put("channelId", channelId);
			}
			return true;
		}
		return false;
	}

}
