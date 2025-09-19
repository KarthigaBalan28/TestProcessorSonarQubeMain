package com.hid.idv.service;

import java.util.HashMap;

import com.hid.idv.utils.IDVConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetSubmissionDetailsPreprocessor implements DataPreProcessor2 {

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		String accountAccessKey = GetConfProperties.getProperty(request, IDVConstants.IDV_ACCOUNT_ACCESS_KEY);
		String secretToken = GetConfProperties.getProperty(request, IDVConstants.IDV_SECRET_TOKEN_KEY);
		if(accountAccessKey.isEmpty() || secretToken.isEmpty()) {
			result.addErrMsgParam(IDVConstants.IDV_CONFIG_MISSING);
			result.addOpstatusParam(-1);
			result.addHttpStatusCodeParam(401);
			return false;
		}
		request.addRequestParam_(IDVConstants.ACCOUNT_KEY_PARAM, accountAccessKey);
		request.addRequestParam_(IDVConstants.SECRET_TOKEN_PARAM, secretToken);
	    return true;
	}

}
