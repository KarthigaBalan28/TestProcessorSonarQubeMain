package com.hid.smsdelivery.preprocessor;

import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class SendOTPPreprocessorKMS extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.smsdelivery.preprocessor.SendOTPPreprocessorKMS.class);

	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if (super.execute(inputMap, request, response, result)) {

			LOG.debug("HID : In SendOTPPreprocessorKMS");
			String transactionId = Objects.toString(inputMap.get("msgId"), "");
			if("".equals(transactionId)) {
				result.addOpstatusParam(-1);
				result.addParam("sendOOBServiceError", AuthenticationConstants.INVALID_AUTH_KEY);
				return false;
			}
			String cachetxKey = Objects
					.toString(request.getServicesManager().getResultCache().retrieveFromCache(transactionId), null);	
			if (cachetxKey != null && !cachetxKey.isEmpty() && cachetxKey.equals("true")) {		
				LOG.debug("HID : cachetxKey is present");
				request.setAttribute("transactionId", transactionId);				
			} else {
				result.addOpstatusParam(-1);
				result.addStringParam("sendOOBServiceError", AuthenticationConstants.INVALID_AUTH_KEY);
				result.addErrMsgParam("Invalid request payload");
				return false;
			}
			return true;
		}
		return false;
	}

}