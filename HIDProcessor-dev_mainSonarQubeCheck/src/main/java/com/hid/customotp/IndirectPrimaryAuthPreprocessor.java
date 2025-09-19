package com.hid.customotp;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class IndirectPrimaryAuthPreprocessor extends ClientBasePreprocessor implements DataPreProcessor2{

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if(super.execute(inputMap, request, response, result)) {
			String token = request.getParameter("Authorization");
			String[] tokenArr= token.split(" ");
			inputMap.put("token" , tokenArr[tokenArr.length-1]);
			String authType = GetConfProperties.getProperty(request, AuthenticationConstants.HID_OTP_SMS_ENV_VARIABLE_KEY);
			if(!StringUtils.isEmpty(authType)) {
				inputMap.put(CustomOTPConstants.AUTH_TYPE_PARAM, authType);
			}
			return true;
		}
		return false;
	}
     
}
