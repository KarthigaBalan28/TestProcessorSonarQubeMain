package com.hid.identity.validator;

import java.util.HashMap;
import java.util.Objects;

import com.hid.identity.util.ServiceUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class NoMfaValidator implements AuthFactorValidator {

	@Override
	public boolean validate(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) {
		result.addOpstatusParam(0);
		result.addHttpStatusCodeParam(200);
		request.setAttribute("validatorStatus", "success");
		String mfaKey = Objects.toString(inputMap.get("mfa_key"), "");
		if(!mfaKey.isEmpty())ServiceUtils.removeFromCache(request,mfaKey);
		return true;
	}

	
	
}
