package com.hid.usermanagement.preprocessor;

import java.util.HashMap;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetPasswordPolicyPreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if (super.execute(inputMap, request, response, result)) {
			String authType = GetConfProperties.getProperty(request, AuthenticationConstants.HID_PASSWORD_AUTHTYPE);		
			if (!authType.isEmpty()) {
				inputMap.put("authType", authType);
			}
			return true;
		}
		return false;
	}
}
