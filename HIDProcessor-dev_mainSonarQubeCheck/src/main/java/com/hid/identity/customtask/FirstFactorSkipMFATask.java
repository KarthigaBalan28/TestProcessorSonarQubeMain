package com.hid.identity.customtask;

import java.util.HashMap;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class FirstFactorSkipMFATask implements IdentityTask{

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) {
		result.addParam(new Param("is_mfa_enabled", "false", "boolean"));
		return true;
	}

}
