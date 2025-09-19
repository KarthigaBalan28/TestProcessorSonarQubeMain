package com.hid.onboarding.preprocessor;

import java.util.HashMap;
import java.util.Objects;

import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.AuthenticationConstants;
import com.hid.util.HIDFabricConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class AddOOBAuthenticatorPreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.preprocessor.AddOOBAuthenticatorPreProcessor.class);

	@SuppressWarnings({"java:S1125", "java:S1481", "java:S1854", "java:S3776","java:S1185"})
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if (super.execute(inputMap, request, response, result)) {
			LOG.debug("HID : In AddOOBAuthenticatorPreProcessor");
			String userId = Objects.toString(inputMap.get("userId"), "");
			boolean isPasswordAdded = request.getAttribute("isPasswordAdded") == null ? true
					: request.getAttribute("isPasswordAdded");
			LOG.debug("HID : value of isPasswordAdded is : {}", isPasswordAdded);
			if (!isPasswordAdded) {
				LOG.debug("HID : Password authenticator already exists for user, returning false");
				result.addOpstatusParam(-1);
				result.addErrMsgParam(HIDFabricConstants.PWD_EXISTS);
				return false;
			}
			String oob = inputMap.get("AuthenticatorType").toString();
			String authenticatorType = oob.equals("AT_OOBSMS") ? AuthenticationConstants.HID_OTP_SMS_ENV_VARIABLE_KEY : AuthenticationConstants.HID_OTP_EML_ENV_VARIABLE_KEY;
			LOG.debug("HID : Getting value of {} from server settings", authenticatorType);
			String authType = GetConfProperties.getProperty(request, authenticatorType);
			LOG.debug("HID : Value of {} from server settings is : {}",authenticatorType, authType);
			request.setAttribute("userId", userId);
			if (!authType.isEmpty()) {
				LOG.debug("HID : Setting the value of AuthenticatorType in input parameter");
				inputMap.put("AuthenticatorType", authType);
			}
			String authenticatorValue = inputMap.get("AuthenticatorValue").toString();
			String deviceType = "";
			if(authenticatorValue != null && !authenticatorValue.isEmpty()) {
				deviceType = authenticatorValue.equals("DT_OOBSMS") ? AuthenticationConstants.HID_SMS_DEVICE_TYPE
						: AuthenticationConstants.HID_EMAIL_DEVICE_TYPE;
				
			}else {
				deviceType = oob.equals("AT_OOBSMS") ? AuthenticationConstants.HID_SMS_DEVICE_TYPE
						: AuthenticationConstants.HID_EMAIL_DEVICE_TYPE;
			}	
			deviceType = GetConfProperties.getProperty(request, deviceType);
			if (deviceType != null && deviceType.isEmpty()) {
				LOG.debug("HID : Setting the value of deviceType in input parameter");
				inputMap.put("AuthenticatorValue", deviceType);
			}
			String isPasswordRequired = Objects.toString(inputMap.get("isPasswordRequired"),"false");
			if("false".equals(isPasswordRequired)) {
				inputMap.put("OOB_PIN", "null");
			} 
			return true;
		}
		return false;
	}

}
