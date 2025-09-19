package com.hid.smsdelivery.preprocessor;

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

public class GenerateOTPPreprocessor extends ClientBasePreprocessor implements DataPreProcessor2 {
	private static final Logger LOG = LogManager
			.getLogger(com.hid.smsdelivery.preprocessor.GenerateOTPPreprocessor.class);

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if (super.execute(inputMap, request, response, result)) {
			LOG.error("HID : In GenerateOTPPreprocessor");
			String correlationId = Objects.toString(inputMap.get("correlationId"), "");
			request.addRequestParam_("X-Correlation-ID", correlationId);
			
			String oobType = Objects.toString(inputMap.get("AuthenticationType")); 
			String authenticatorType = oobType.equals("AT_OOBSMS") ? AuthenticationConstants.HID_OTP_SMS_ENV_VARIABLE_KEY : AuthenticationConstants.HID_OTP_EML_ENV_VARIABLE_KEY;
			LOG.error("HID : Getting value of authenticatorType:{} from server settings", authenticatorType);
			String authType = GetConfProperties.getProperty(request, authenticatorType);
			LOG.error("HID : Value of authenticatorType:{} from server settings is :{} ", authenticatorType, authType);
			if (!authType.isEmpty()) {
				LOG.error("HID : Setting the value of AuthenticatorType in input parameter");
				inputMap.put("AuthenticationType", authType);
			}
			return true;
		}
		return false;

	}
}
