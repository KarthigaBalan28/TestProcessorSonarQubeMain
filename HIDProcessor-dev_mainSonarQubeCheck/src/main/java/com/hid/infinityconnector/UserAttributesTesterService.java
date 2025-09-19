package com.hid.infinityconnector;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.hid.services.CustomerAttributesService;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class UserAttributesTesterService implements JavaService2 {

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		String username = Objects.toString(((Map<?, ?>) inputArray[1]).get("userName"), null);
		if(StringUtils.isEmpty(username)) {
			result.addStringParam("Error", "Username is not present in the request");
			result.addOpstatusParam(-1);
			result.addHttpStatusCodeParam(403);
			return result;
		}
		CustomerAttributesService caService = new CustomerAttributesService();
		caService.populateUserAttributes(result,username, request);
		return result;
	}

}
