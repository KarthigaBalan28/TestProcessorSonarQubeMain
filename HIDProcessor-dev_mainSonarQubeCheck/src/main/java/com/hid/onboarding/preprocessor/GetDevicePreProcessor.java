package com.hid.onboarding.preprocessor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetDevicePreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {
	
	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.preprocessor.GetDevicePreProcessor.class);
	
	private static final String GET_DEVICE_SERVICE_ERROR_PARAM = "GetDeviceServiceError";
	private static final String INVALID_REQUEST_PAYLOAD_PARAM = "Invalid request payload";
	
	@SuppressWarnings("java:S1185")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if (super.execute(inputMap, request, response, result)) {

			String authKey = Objects.toString(inputMap.get("auth_key"), "");
			String deviceId = Objects.toString(inputMap.get("deviceId"), "");
			
			request.setAttribute("auth_key", authKey);
			request.setAttribute("deviceId", deviceId);
			
			
			if ("".equals(authKey)) {
				result.addOpstatusParam(-1);
				result.addStringParam(GET_DEVICE_SERVICE_ERROR_PARAM, AuthenticationConstants.INVALID_AUTH_KEY);
				result.addErrMsgParam(INVALID_REQUEST_PAYLOAD_PARAM);
				return false;
			}

			String cacheAuthKey = Objects
					.toString(request.getServicesManager().getResultCache().retrieveFromCache(authKey));
			if (cacheAuthKey != null && !cacheAuthKey.isEmpty() && cacheAuthKey.equals(deviceId)) {
				
				LOG.debug("HID : cacheAuthKey is present");
				request.setAttribute("cacheAuthKey", authKey);
				LOG.debug("HID : In GetDevice");
	
				return true;
			} else {
				result.addOpstatusParam(-1);
				result.addStringParam(GET_DEVICE_SERVICE_ERROR_PARAM, AuthenticationConstants.INVALID_AUTH_KEY);
				result.addErrMsgParam(INVALID_REQUEST_PAYLOAD_PARAM);
				return false;
			}
		} else {
			result.addOpstatusParam(-1);
			result.addStringParam(GET_DEVICE_SERVICE_ERROR_PARAM, AuthenticationConstants.INVALID_AUTH_KEY);
			result.addErrMsgParam(INVALID_REQUEST_PAYLOAD_PARAM);
			return false;
		}
	}
}
