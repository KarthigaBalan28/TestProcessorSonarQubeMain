package com.hid.usermanagement.preprocessor;

import java.util.HashMap;

import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class SearchUserPreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {
	private static final Logger LOG = LogManager.getLogger(com.hid.usermanagement.preprocessor.SearchUserPreProcessor.class);

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if (super.execute(inputMap, request, response, result)) {
			LOG.debug("HID : In SelfServiceSearchUserPreProcessor");
			String authType = GetConfProperties.getProperty(request, AuthenticationConstants.HID_PASSWORD_AUTHTYPE);
			LOG.debug("HID : value of authType is : {}", authType);
			 LOG.debug("HID : AuthType from Server property is : {}", authType);
			if (!authType.isEmpty()) {
				LOG.debug("HID : Setting the value of AuthenticatorType in input parameter");
				inputMap.put("authType", authType);
				request.setAttribute("authType", authType);
			}
			
			return true;
		}
		return false;
}

}
