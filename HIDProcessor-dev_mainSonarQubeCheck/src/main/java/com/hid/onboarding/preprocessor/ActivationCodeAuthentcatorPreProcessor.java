package com.hid.onboarding.preprocessor;

import java.util.HashMap;

import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.HIDFabricConstants;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class ActivationCodeAuthentcatorPreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.preprocessor.ActivationCodeAuthentcatorPreProcessor.class);
	private static final String SEQUENCE_FAILED_PARAM = "sequenceFailed";
	private static final String ERROR_MSG_DETAIL_PARAM = "errorMsgDetail";
	private static final String USERID_PARAM = "userid";

	@SuppressWarnings({"java:S1125","java:S3776"})
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if (super.execute(inputMap, request, response, result)) {
			LOG.debug("HID : In ActivationCodeAuthentcatorPreProcessor");
			Boolean userExists = (request.getAttribute("userExists") == null) ? true : request.getAttribute("userExists");
			Boolean authExists = (request.getAttribute("AuthExists") == null) ? true : request.getAttribute("AuthExists");
			LOG.debug("HID : values of userExists = {} and AuthExists = {}", userExists, authExists);
			String authType = GetConfProperties.getProperty(request, AuthenticationConstants.HID_ACTIVATION_CODE_AUTHTYPE);
			LOG.debug("HID : Value of ACTIVATION_CODE_AUTHTYPE from settings is : {}", authType);
			String userid = request.getAttribute(USERID_PARAM) == null ? "" : request.getAttribute(USERID_PARAM);
			LOG.debug("HID : UserId is : {}", userid);
			if (userExists == null || !userExists || userid.isEmpty()) {
				LOG.debug("HID : Either user doesn't exist or userid is empty, setting User Not Exists error message");
				result.addOpstatusParam(-1);
				result.addErrMsgParam(HIDFabricConstants.USER_NOT_EXIST);
				request.setAttribute(SEQUENCE_FAILED_PARAM, true);
				request.setAttribute(ERROR_MSG_DETAIL_PARAM, HIDFabricConstants.USER_NOT_EXIST);
				return false;
			}
			if (authExists == null || !authExists) {
				LOG.debug("HID : Activation code authenticator doesn't exist for user, setting error message");
				result.addOpstatusParam(-1);
				result.addErrMsgParam(HIDFabricConstants.AUTH_NOT_EXIST);
				request.setAttribute(SEQUENCE_FAILED_PARAM, true);
				request.setAttribute(ERROR_MSG_DETAIL_PARAM, HIDFabricConstants.AUTH_NOT_EXIST);
				return false;
			}
			inputMap.put(USERID_PARAM, userid);
			if (!authType.isEmpty()) {
				LOG.debug("HID : Setting AuthType to {} in input parameter from server settings", authType);
				inputMap.put("authType", authType);
			}
			request.setAttribute(SEQUENCE_FAILED_PARAM, false);
			return true;
		}else {
			return false;
		}
	}

}
