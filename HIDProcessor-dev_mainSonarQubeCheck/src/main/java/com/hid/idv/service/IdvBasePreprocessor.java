package com.hid.idv.service;

import java.util.HashMap;

import com.hid.idv.utils.IDVConstants;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class IdvBasePreprocessor implements DataPreProcessor2 {

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		IdvTokenService service = IdvDependencyManager.getTokenService(request);
		String token = service.getToken();
		if(token.isEmpty()) {
			result.addOpstatusParam(-1);
			result.addHttpStatusCodeParam(401);
			result.addErrMsgParam(IDVConstants.ERROR_TOKEN_INVALID);
			return false;
		}
		request.addRequestParam_("Authorization", "Bearer " + token);
		return true;
	}

}
