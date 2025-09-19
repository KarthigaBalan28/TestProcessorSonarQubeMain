package com.hid.common;

import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.util.HIDFabricConstants;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.hid.util.HIDIntegrationServices;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager.getLogger(com.hid.common.ClientBasePreprocessor.class);

	@SuppressWarnings("java:S2293")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		LOG.debug("HID : In ClientBasePreProcessor");
		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		HashMap<String, Object> emptyHM = new HashMap<String, Object>();
		String appKey = GetConfProperties.getProperty(request, AuthenticationConstants.HID_KONY_APP_KEY);
		String appSecret = GetConfProperties.getProperty(request, AuthenticationConstants.HID_KONY_APP_SECRET);
		headerMap.put("x-kony-app-key", appKey);
		headerMap.put("x-kony-app-secret", appSecret);
		Result authResult = HIDIntegrationServices.call("HIDClientAuthIdentityWrapper", "getClientBearerToken", request,
				headerMap, emptyHM);
		String accessToken = authResult.getParamValueByName("access_token");
		if (!StringUtils.isEmpty(accessToken)) {
			LOG.debug("HID::ClientBasePreProcessor ---> Access token fetched, inserting into cache");
			request.addRequestParam_("Authorization", "Bearer " + accessToken);
			return true;
		}
		LOG.debug("HID::ClientBasePreProcessor ---> Falied to fetch access token");
		result.addOpstatusParam(-1);
		result.addErrMsgParam(HIDFabricConstants.TOKEN_FAILED);
		return false;
	}

}