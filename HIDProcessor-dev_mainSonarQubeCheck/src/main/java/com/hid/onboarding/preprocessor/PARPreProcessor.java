package com.hid.onboarding.preprocessor;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class PARPreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {
	
	private static final Logger LOG = LogManager.getLogger(com.hid.onboarding.preprocessor.PARPreProcessor.class);
	
	@Override
	public boolean execute(
			HashMap inputMap, DataControllerRequest request, DataControllerResponse response, Result result)
					throws Exception
	{
		if (super.execute(inputMap, request, response, result))
		{
			LOG.debug("HID : In PARPreProcessor");
			String clientId = GetConfProperties.getProperty(request, AuthenticationConstants.HID_CLIENT_ID);
			String redirectURI = GetConfProperties.getProperty(request, AuthenticationConstants.HID_REDIRECT_URI);
			
			if (!clientId.isEmpty() && !redirectURI.isEmpty())
			{
				LOG.debug("HID : Setting the value of client_id and redirect_uri in input parameter");				
				inputMap.put("client_id", clientId);
				inputMap.put("redirect_uri", redirectURI);
			}
			else
			{
				result.addOpstatusParam(-1);
				result.addStringParam("PARServiceError", AuthenticationConstants.EMPTY_CID_RURI);
				result.addErrMsgParam("Invalid request payload");
				
				return false;
			}
			
			return true;
		}
		
		return false;
	}

}
