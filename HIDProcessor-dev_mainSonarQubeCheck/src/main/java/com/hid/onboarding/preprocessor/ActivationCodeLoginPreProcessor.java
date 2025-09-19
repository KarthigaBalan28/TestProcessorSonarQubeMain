package com.hid.onboarding.preprocessor;

import java.util.HashMap;
import java.util.Objects;

import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.hid.util.HIDFabricConstants;
import com.hid.common.ClientBasePreprocessor;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class ActivationCodeLoginPreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.preprocessor.ActivationCodeLoginPreProcessor.class);
	
	private static final String SEQUENCE_FAILED_PARAM = "sequenceFailed";

	@SuppressWarnings("java:S1125")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if (super.execute(inputMap, request, response, result)) {
			boolean sequenceFailed = request.getAttribute(SEQUENCE_FAILED_PARAM) == null ? false : request.getAttribute(SEQUENCE_FAILED_PARAM);
			if(sequenceFailed) {
				result.addOpstatusParam(-1);
				result.addErrMsgParam("Sequence Failed");
				return false;
			}
			String authType = GetConfProperties.getProperty(request, AuthenticationConstants.HID_ACTIVATION_CODE_AUTHTYPE);
			if (!authType.isEmpty()) {
				LOG.debug("HID : Setting AuthType to {} in input parameter from server settings", authType);
				inputMap.put("authType", authType);
			}
			String username = Objects.toString(inputMap.get("username"), "");
			request.setAttribute("username", username);
			request.setAttribute(SEQUENCE_FAILED_PARAM, false);
			return true;
		}else {
			return false;
		}
	}

}
