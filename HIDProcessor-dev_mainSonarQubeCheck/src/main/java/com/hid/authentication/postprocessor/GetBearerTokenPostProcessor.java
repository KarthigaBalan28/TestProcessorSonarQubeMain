package com.hid.authentication.postprocessor;

import org.apache.commons.lang3.StringUtils;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetBearerTokenPostProcessor implements DataPostProcessor2 {

	private static final String TOKEN_KEY = "access_token";

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		
		
		String accessToken = result.getParamValueByName(TOKEN_KEY);
		if(!StringUtils.isEmpty(accessToken)) {
			request.setAttribute(TOKEN_KEY, "Bearer " + accessToken);
		}else {
			request.setAttribute(TOKEN_KEY, "");
		}
		return result;
	}

}
