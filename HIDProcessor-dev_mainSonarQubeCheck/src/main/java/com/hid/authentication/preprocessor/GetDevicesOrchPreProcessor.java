package com.hid.authentication.preprocessor;

import java.util.HashMap;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hid.util.AuthenticationConstants;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetDevicesOrchPreProcessor implements DataPreProcessor2 {
	private static final Logger LOG = LogManager
			.getLogger(com.hid.authentication.preprocessor.GetDevicesOrchPreProcessor.class);

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		LOG.debug("HID:: Inside the GetDevicesOrchPreProcessor");
		String transactionId = Objects.toString(inputMap.get("msgId"), "");
		if("".equals(transactionId)) {			
			result.addOpstatusParam(-1);
			result.addStringParam("searchDevicesServiceError", AuthenticationConstants.INVALID_AUTH_KEY);
			result.addErrMsgParam("Invalid request payload");
			return false;
		}

		String cachetxKey = Objects
				.toString(request.getServicesManager().getResultCache().retrieveFromCache(transactionId), null);
		if (cachetxKey != null && !cachetxKey.isEmpty() && cachetxKey.equals("true")) {		
			LOG.debug("HID : cachetxKey is present");
			request.setAttribute("transactionId", transactionId);		
			request.setAttribute("isLoginFlow", "true");
		} else {
			result.addOpstatusParam(-1);
			result.addStringParam("searchDevicesServiceError", AuthenticationConstants.INVALID_AUTH_KEY);
			result.addErrMsgParam("Invalid request payload");
			return false;
		}
		return true;
	}

}
