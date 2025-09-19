package com.hid.authentication.preprocessor;

import java.util.HashMap;

import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class HardwareOTPAuthPreprocessor implements DataPreProcessor2 {

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		String authType = GetConfProperties.getProperty(request, AuthenticationConstants.HID_HARDWARE_OTP_AUTHTYPE);
		if(!authType.isEmpty()) {
			inputMap.put("authType",authType);
		}else {
			inputMap.put("authType",AuthenticationConstants.HW_OTP_AUTHTYPE);
		}
		return true;
	}

}
