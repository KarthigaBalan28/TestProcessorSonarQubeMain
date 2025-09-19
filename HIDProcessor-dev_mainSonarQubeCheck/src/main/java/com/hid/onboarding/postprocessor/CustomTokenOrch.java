package com.hid.onboarding.postprocessor;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class CustomTokenOrch implements DataPostProcessor2 {
	
	private static final Logger LOG = LogManager.getLogger(com.hid.onboarding.postprocessor.CustomTokenOrch.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : In CustomTokenOrch");
		String accessToken = result.getParamValueByName("org_admin_token");
		if (!StringUtils.isEmpty(accessToken)) {
			LOG.debug("HID : Org admin access token is not null, setting bearer token in the result parameter");
			Record hidUsrAttr = new Record();
			hidUsrAttr.setId("user_attributes");
			hidUsrAttr.addStringParam("user_id",accessToken );
			result.addRecord(hidUsrAttr);
			Record hidSessionAttr = new Record();
			hidSessionAttr.setId("security_attributes");
			hidSessionAttr.addStringParam("session_token",accessToken);
			hidSessionAttr.addStringParam("access_token", "Bearer "+ accessToken);
			result.addRecord(hidSessionAttr);
		}
		return result;
	}

}
