package com.hid.idv.service;

import java.util.HashMap;

import com.hid.idv.utils.IDVConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetTokenPreprocessor implements DataPreProcessor2{

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		String username = GetConfProperties.getProperty(request, IDVConstants.IDV_USERNAME_KEY);
		String password = GetConfProperties.getProperty(request, IDVConstants.IDV_PASSWORD_KEY);
		if(username.isEmpty() || password.isEmpty()) {
			result.addErrMsgParam(IDVConstants.IDV_CONFIG_MISSING);
			result.addOpstatusParam(-1);
			result.addHttpStatusCodeParam(403);
			return false;
		}
	    inputMap.put(IDVConstants.GETTOKEN_USERNAME_PARAM, username);
	    inputMap.put(IDVConstants.GETTOKEN_PASSWORD_PARAM, password);
	    return true;
	}

}
	