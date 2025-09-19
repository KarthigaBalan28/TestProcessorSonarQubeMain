package com.hid.onboarding.preprocessor;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class OrgAdminPreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager.getLogger(com.hid.onboarding.preprocessor.OrgAdminPreprocessor.class);

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		LOG.debug("HID : In OrgAdminPreprocessor");
		String orgAdminUsername = GetConfProperties.getProperty(request, AuthenticationConstants.HID_ORG_ADMIN_USERNAME);
		String orgAdminPassword = GetConfProperties.getProperty(request, AuthenticationConstants.HID_ORG_ADMIN_PASSWORD);
		LOG.debug("HID : Getting values of ORG_ADMIN_USERNAME and ORG_ADMIN_PASSWORD from server settings");
		if (StringUtils.isEmpty(orgAdminPassword) || StringUtils.isEmpty(orgAdminUsername)) {
			LOG.debug("HID : HID_ORG_ADMIN_USERNAME or HID_ORG_ADMIN_PASSWORD is null, setting error message");
			result.addErrMsgParam("Configurable Properties are not defined");
			return false;
		}
		inputMap.put("orgAdminId", orgAdminUsername);
		inputMap.put("orgAdminPwd", orgAdminPassword);
		return true;
	}

}
