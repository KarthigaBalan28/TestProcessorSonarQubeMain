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

public class GetInviteCodePreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.preprocessor.GetInviteCodePreProcessor.class);
	
	private static final String GET_INVITE_CODE_SERVICE_ERROR_PARAM = "GetInviteCodeServiceError";

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {

		if (super.execute(inputMap, request, response, result)) {		
			LOG.debug("HID : In GetInviteCodePreProcessor");			
			String authKey = Objects.toString(inputMap.get("Auth_Key"),"");
			String userId = Objects.toString(inputMap.get("UserId"),"");
			
			String correlationId = Objects.toString(inputMap.get("correlationId"), "");
			request.getHeaderMap().put("X-Correlation-ID", correlationId);
			
			String userIdHashCode = Integer.toString(userId.hashCode());
			if ("".equals(authKey)) {
				result.addOpstatusParam(-1);
				result.addStringParam(GET_INVITE_CODE_SERVICE_ERROR_PARAM, AuthenticationConstants.INVALID_AUTH_KEY);
				request.setAttribute("sequenceFailed", true);
				request.setAttribute("errorMsgDetail", AuthenticationConstants.INVALID_AUTH_KEY);
				return false;
			}
			
			
			String cacheAuthKey = Objects
					.toString(request.getServicesManager().getResultCache().retrieveFromCache(authKey));
			if (cacheAuthKey != null && !cacheAuthKey.isEmpty() && cacheAuthKey.equals(userIdHashCode)) {		
				LOG.debug("HID : cacheAuthKey is present");
				request.setAttribute("cacheAuthKey", authKey);
				request.setAttribute("isOnboardingFlow", "true");
				return true;
			} else {
				result.addOpstatusParam(-1);
				result.addStringParam(GET_INVITE_CODE_SERVICE_ERROR_PARAM, AuthenticationConstants.INVALID_AUTH_KEY);
				result.addErrMsgParam("Invalid request payload");
				return false;
			}
		} else {
			result.addOpstatusParam(-1);
			result.addStringParam(GET_INVITE_CODE_SERVICE_ERROR_PARAM, AuthenticationConstants.INVALID_AUTH_KEY);
			result.addErrMsgParam("Invalid request payload");
			return false;
		}
	}
}
