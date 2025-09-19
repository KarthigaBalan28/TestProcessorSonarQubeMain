package com.hid.transactionsigning.preprocessor;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class SignatureValidationPreprocessor extends ClientBasePreprocessor implements DataPreProcessor2{

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		 if (super.execute(inputMap, request, response, result)) {
			String authType = GetConfProperties.getProperty(request,
					AuthenticationConstants.HID_SECURE_CODE_ENV_VARIABLE_KEY);
			if (!StringUtils.isEmpty(authType)) {
				inputMap.put("authType", authType);
			} 
			String channelId = GetConfProperties.getProperty(request, AuthenticationConstants.HID_IDP_CHANNEL_ENV_VARIABLE_KEY);
			if(!StringUtils.isEmpty(channelId)) {
				inputMap.put("channelId",channelId);
			}
            return true;
		}
	    return false;
	}
}
