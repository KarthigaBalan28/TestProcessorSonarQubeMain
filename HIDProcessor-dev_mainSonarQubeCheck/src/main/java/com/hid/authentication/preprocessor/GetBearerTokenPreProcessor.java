package com.hid.authentication.preprocessor;

import java.util.HashMap;

import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.util.HIDFabricConstants;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetBearerTokenPreProcessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.authentication.preprocessor.GetBearerTokenPreProcessor.class);

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		LOG.debug("HID : In GetBearerTokenPreProcessor");
		String appKey = GetConfProperties.getProperty(request, AuthenticationConstants.HID_KONY_APP_KEY);
		String appSecret = GetConfProperties.getProperty(request, AuthenticationConstants.HID_KONY_APP_SECRET);
		LOG.debug("HID : Value of App key and App Secret is : {} and {}", appKey, appSecret);

		if (appKey.isEmpty() || appSecret.isEmpty()) {
			LOG.debug("HID : Empty value of app key and app secret, returning error");
			result.addErrMsgParam(HIDFabricConstants.APP_DETAILS_NOT_EXIST);
			result.addHttpStatusCodeParam(401);
			result.addOpstatusParam(-1);
		}
		
		request.addRequestParam_("x-kony-app-key", appKey);
		request.addRequestParam_("x-kony-app-secret", appSecret);
		return true;

	}

}
