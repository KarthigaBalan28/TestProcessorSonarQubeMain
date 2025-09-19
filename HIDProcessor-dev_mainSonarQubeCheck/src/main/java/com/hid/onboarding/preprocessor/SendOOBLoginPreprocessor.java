package com.hid.onboarding.preprocessor;

import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class SendOOBLoginPreprocessor extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.preprocessor.SendOOBLoginPreprocessor.class);

	@SuppressWarnings({"java:S1161","java:S1185"})
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if (super.execute(inputMap, request, response, result)) {

			LOG.debug("HID : In SendOOBLoginPreprocessor");
			String transactionId = Objects.toString(inputMap.get("msgId"), "");
			if("".equals(transactionId)) {
				result.addOpstatusParam(-1);
				result.addParam("sendOOBServiceError", AuthenticationConstants.INVALID_AUTH_KEY);
				return false;
			}
			String cachetxKey = Objects
					.toString(request.getServicesManager().getResultCache().retrieveFromCache(transactionId), null);
			
			
			if (cachetxKey != null && !cachetxKey.isEmpty() && cachetxKey.equals("true")) {		
				LOG.debug("HID : cachetxKey is present");
				request.setAttribute("transactionId", transactionId);				
			} else {
				result.addOpstatusParam(-1);
				result.addStringParam("sendOOBServiceError", AuthenticationConstants.INVALID_AUTH_KEY);
				result.addErrMsgParam("Invalid request payload");
				return false;
			}
			String oob = Objects.toString(inputMap.get("AuthenticationType"), "");
			String authenticatorType = oob.equals("AT_OOBSMS") ? AuthenticationConstants.HID_OTP_SMS_ENV_VARIABLE_KEY : AuthenticationConstants.HID_OTP_EML_ENV_VARIABLE_KEY;
			LOG.debug("HID : Getting value of {} from server settings", authenticatorType);
			String authType = GetConfProperties.getProperty(request, authenticatorType);
			LOG.debug("HID : Value of {} from server settings is : {}",authenticatorType ,authType);
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