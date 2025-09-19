package com.hid.onboarding.preprocessor;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class PasswordValidationPreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.preprocessor.PasswordValidationPreprocessor.class);

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		LOG.debug("HID : In PasswordValidationPreprocessor");
		String accessToken = request.getAttribute("access_token");
		if (!StringUtils.isEmpty(accessToken)) {
			inputMap.put("Authorization", accessToken);
			request.addRequestParam_("Authorization", accessToken);
			LOG.debug("HID : Got access token, returning true");
			return true;
		}
		LOG.debug("HID : Access token is null, Unauthorized and returning false");
		result.addOpstatusParam(-1);
		result.addHttpStatusCodeParam(401);
		result.addErrMsgParam("UnAuthorized");
		return false;
	}

}
