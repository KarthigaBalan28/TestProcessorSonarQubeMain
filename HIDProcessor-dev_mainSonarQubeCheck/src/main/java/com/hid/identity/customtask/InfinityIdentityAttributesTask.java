package com.hid.identity.customtask;

import java.util.HashMap;
import java.util.Objects;

import com.hid.services.CustomerAttributesService;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;


public class InfinityIdentityAttributesTask implements IdentityTask{

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) {
		String username = Objects.toString(inputMap.get("username"),"");
		CustomerAttributesService caService = new CustomerAttributesService();
		caService.populateUserAttributes(result,username, request);
		return true;
	}

}
