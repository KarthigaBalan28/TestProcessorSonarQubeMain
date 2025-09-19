package com.hid.onboarding.preprocessor;

import java.util.HashMap;
import java.util.Objects;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.AuthenticationConstants;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class AddAllOOBAuthenticatorsPrePreprocessor extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.preprocessor.AddAllOOBAuthenticatorsPrePreprocessor.class);
	
	private static final String SEQUENCE_FAILED_PARAM = "sequenceFailed";
	private static final String ADD_ALL_OOB_SERVICE_ERROR = "AddAllOOBServiceError";
	private static final String INVALID_REQUEST_PAYLOAD_PARAM = "Invalid request payload";

	@SuppressWarnings({"java:S1854","java:S1125","java:S1481"})
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {

		if (super.execute(inputMap, request, response, result)) {		
			LOG.debug("HID : In AddAllOOBAuthenticatorsPrePreprocessor");
			boolean sequenceFailed = request.getAttribute(SEQUENCE_FAILED_PARAM) == null ? false : request.getAttribute(SEQUENCE_FAILED_PARAM);
			String authKey = Objects.toString(inputMap.get("Auth_Key"), "");
			String username = Objects.toString(inputMap.get("username"), "");
			String userId = Objects.toString(inputMap.get("userId"), "");
			String factor = Objects.toString(inputMap.get("factor"), "");
			
			String correlationId = Objects.toString(inputMap.get("correlationId"), "");
			request.getHeaderMap().put("X-Correlation-ID", correlationId);
			
			String userIdHashCode = Integer.toString(userId.hashCode());
			if ("".equals(authKey)) {
				result.addOpstatusParam(-1);
				result.addStringParam(ADD_ALL_OOB_SERVICE_ERROR, AuthenticationConstants.INVALID_AUTH_KEY);
				result.addErrMsgParam(INVALID_REQUEST_PAYLOAD_PARAM);
				return false;
			}
			
			String cacheAuthKey = Objects
					.toString(request.getServicesManager().getResultCache().retrieveFromCache(authKey));
			if (cacheAuthKey != null && !cacheAuthKey.isEmpty() && cacheAuthKey.equals(userIdHashCode)) {		
				LOG.debug("HID : cacheAuthKey is present");
				request.setAttribute(SEQUENCE_FAILED_PARAM, false);
				request.setAttribute("cacheAuthKey", authKey);
				request.setAttribute("username", username);
				request.setAttribute("userId", userId);
				request.setAttribute("factor", factor);
				return true;
			} else {
				result.addOpstatusParam(-1);
				result.addStringParam(ADD_ALL_OOB_SERVICE_ERROR, AuthenticationConstants.INVALID_AUTH_KEY);
				result.addErrMsgParam(INVALID_REQUEST_PAYLOAD_PARAM);
				return false;
			}
		} else {
			result.addOpstatusParam(-1);
			result.addStringParam(ADD_ALL_OOB_SERVICE_ERROR, AuthenticationConstants.INVALID_AUTH_KEY);
			result.addErrMsgParam(INVALID_REQUEST_PAYLOAD_PARAM);
			return false;
		}
	}
}
