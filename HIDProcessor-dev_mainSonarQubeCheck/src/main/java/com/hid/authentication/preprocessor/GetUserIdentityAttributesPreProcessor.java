package com.hid.authentication.preprocessor;

import java.util.HashMap;

import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.util.AuthenticationConstants;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetUserIdentityAttributesPreProcessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.authentication.preprocessor.GetUserIdentityAttributesPreProcessor.class);

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		LOG.debug("HID : In GetUserIdentityAttributesPreProcessor");
		boolean isSequenceFailed = (request.getAttribute("sequenceFailed") == null) ? false : request.getAttribute("sequenceFailed"); //NOSONAR
		String username = (request.getAttribute("username") == null ) ? "" : request.getAttribute("username");
		if(!username.isEmpty()) {
			inputMap.put(AuthenticationConstants.USERATTRIBUTES_NAME_PARAM, username);
		}
		LOG.debug("HID : MFA Validation : {}", !isSequenceFailed);
		if(isSequenceFailed)
		{
			LOG.debug("HID : Mfa is not validated, not executing GetUserIdentityAttributes service");
			return false;
		}
		return true;
	}
}
