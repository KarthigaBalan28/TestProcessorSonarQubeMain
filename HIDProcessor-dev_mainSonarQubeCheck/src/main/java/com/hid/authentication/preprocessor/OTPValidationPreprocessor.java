package com.hid.authentication.preprocessor;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class OTPValidationPreprocessor implements DataPreProcessor2 {

	private static final String AUTHYPE_PARAM = "authType";

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		String oob = inputMap.get(AUTHYPE_PARAM).toString();
		String authenticatorType = "";
		if (oob.equals("OTP_SMS")) {
			authenticatorType = AuthenticationConstants.HID_OTP_SMS_ENV_VARIABLE_KEY;
		} else if (oob.equals("OTP_EML")) {
			authenticatorType = AuthenticationConstants.HID_OTP_EML_ENV_VARIABLE_KEY;
		} else {
			authenticatorType =AuthenticationConstants.HID_SECURE_CODE_ENV_VARIABLE_KEY;
		}

		String authType = GetConfProperties.getProperty(request, authenticatorType);
		if (!authType.isEmpty()) {
			inputMap.put(AUTHYPE_PARAM, authType);
		} else {
			if (oob.equals("OTP_SMS")) {
				authType = AuthenticationConstants.OTP_SMS_AUTHTYPE;
			} else if (oob.equals("OTP_EML")) {
				authType = AuthenticationConstants.OTP_EML_AUTHTYPE;
			} else {
				authType = AuthenticationConstants.SECURE_CODE_AUTHTYPE;
			}

			inputMap.put(AUTHYPE_PARAM, authType);
		}	
		String channelId = GetConfProperties.getProperty(request, AuthenticationConstants.HID_IDP_CHANNEL_ENV_VARIABLE_KEY);
		if(!StringUtils.isEmpty(channelId)) {
			inputMap.put("channelId",channelId);
		}
		return true;
	}

}
