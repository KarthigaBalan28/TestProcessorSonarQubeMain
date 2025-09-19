package com.hid.usermanagement.preprocessor;

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
@SuppressWarnings({"java:S1125"})
public class ChangePasswordPreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager.getLogger(com.hid.usermanagement.preprocessor.ChangePasswordPreProcessor.class);
	private static final String USER_ID = "userId";
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if (super.execute(inputMap, request, response, result)) {
			LOG.debug("HID : In ChangePasswordPreProcessor");
			boolean userExists = (request.getAttribute("userExists") == null) ? true
					: request.getAttribute("userExists");
			LOG.debug("HID : values of userExists = {}", userExists);
			boolean authExists = (request.getAttribute("AuthExists") == null) ? true
					: request.getAttribute("AuthExists");
			LOG.debug("HID : values of authExists = {}", authExists);
			String authType = GetConfProperties.getProperty(request, AuthenticationConstants.HID_PASSWORD_AUTHTYPE);
			LOG.debug("HID : values of authType = {}", authType);
			inputMap.put("authType", authType);
			String userId = request.getAttribute(USER_ID) == null ? "" : request.getAttribute(USER_ID);		    
			LOG.debug("HID : UserId is : {}", userId);
			if (!userExists || userId.isEmpty()) {
				LOG.debug("HID : Either user doesn't exist or userid is empty, setting User Not Exists error message");
				result.addOpstatusParam(-1);
				result.addErrMsgParam(HIDFabricConstants.USER_NOT_EXIST);
				request.setAttribute("sequenceFailed", true);
				request.setAttribute("errorMsgDetail", HIDFabricConstants.USER_NOT_EXIST);
				return false;
			}
			if (!authExists) {
				LOG.debug("HID : Auth doesn't exist");
				result.addOpstatusParam(-1);
				result.addErrMsgParam("Static password Authenticator does not exist");
				request.setAttribute("sequenceFailed", true);
				request.setAttribute("errorMsgDetail", "Static password Authenticator does not exist");
				return false;
			}
			String id = userId + "." + authType;
			inputMap.put(USER_ID, userId);
			inputMap.put("id", id);
			return true;
		}
		return false;

	}
}