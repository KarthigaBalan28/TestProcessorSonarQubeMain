package com.hid.authentication.preprocessor;

import java.util.HashMap;

import org.apache.logging.log4j.Logger; 
import org.apache.logging.log4j.LogManager;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class SearchDeviceAuthPreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.authentication.preprocessor.SearchDeviceAuthPreProcessor.class);

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if (super.execute(inputMap, request, response, result)) {
			LOG.debug("HID : In SearchDeviceAuthPreProcessor");
			boolean userExists = (request.getAttribute("UserExist") == null) ? true : request.getAttribute("UserExist"); //NOSONAR
			LOG.debug("HID : Value of user exists is : {}", userExists);
			if (!userExists) {
				LOG.debug("HID : User does not exists, returning false");
				request.setAttribute("sequenceFailed", true);
				request.setAttribute("errorMsgDetail", HIDFabricConstants.USER_NOT_EXIST);
				return false;
			}
			return true;
		}
		return false;
	}

}
