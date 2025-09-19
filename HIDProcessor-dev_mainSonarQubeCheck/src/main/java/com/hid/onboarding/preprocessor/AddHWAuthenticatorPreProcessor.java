package com.hid.onboarding.preprocessor;

import java.util.HashMap;
import java.util.Objects;

import org.apache.logging.log4j.Logger; 
import org.apache.logging.log4j.LogManager;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class AddHWAuthenticatorPreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.preprocessor.AddHWAuthenticatorPreProcessor.class);

	@SuppressWarnings("java:S1125")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if (super.execute(inputMap, request, response, result)) {

			LOG.debug("HID : In AddHWAuthenticatorPreProcessor");
			boolean sequenceFailed = request.getAttribute("sequenceFailed") == null ? false
					: request.getAttribute("sequenceFailed");
			if (sequenceFailed) {
				result.addOpstatusParam(-1);
				result.addErrMsgParam("Sequence Failed");
				return false;
			}

			String authType = GetConfProperties.getProperty(request, "HWT_AUTHTYPE");
			
			String correlationId = Objects.toString(inputMap.get("correlationId"), "");
			request.addRequestParam_("X-Correlation-ID", correlationId);

			if (!authType.isEmpty()) {
				LOG.debug("HID : Setting the value of AuthenticatorType in input parameter");
				inputMap.put("AuthenticatorType", authType);
			}

			return true;

		}
		return false;
	}

}
