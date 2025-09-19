package com.hid.identity.customtask;

import java.util.HashMap;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class HIDUserAttributesTask implements IdentityTask {

	private static final String DUMMY_PARAM = "dummy";

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) {
		Record hidSessionAttr = new Record();
		hidSessionAttr.setId("security_attributes");
		hidSessionAttr.addStringParam("session_token", DUMMY_PARAM);
		hidSessionAttr.addStringParam("access_token", DUMMY_PARAM);
		Record hidUsrAttr = new Record();
		hidUsrAttr.setId("user_attributes");
		hidUsrAttr.addStringParam("user_id", DUMMY_PARAM);
		result.addRecord(hidUsrAttr);
		result.addRecord(hidSessionAttr);
		return false;
	}

}
