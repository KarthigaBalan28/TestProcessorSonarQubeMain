package com.hid.onboarding.preprocessor;

import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class RegisterCredentialsPreProcessor implements DataPreProcessor2 {
	
	private static final Logger LOG = LogManager.getLogger(com.hid.onboarding.preprocessor.RegisterCredentialsPreProcessor.class);
	
	@Override
	public boolean execute(
			HashMap inputMap, DataControllerRequest request, DataControllerResponse response, Result result)
					throws Exception
	{
		LOG.debug("HID : In RegisterCredentialsPreProcessor");
		String serverCsrfToken = inputMap.get("csrf") == null ? "" : inputMap.get("csrf").toString();
		
		if(!StringUtils.isEmpty(serverCsrfToken))
		{
			request.getHeaderMap().put("server-csrf-token", serverCsrfToken);
		}
		else
		{
			result.addOpstatusParam(-1);
			result.addStringParam("RegisterCredentialServiceError", "server-csrf-token is missing");
			result.addErrMsgParam("Invalid header payload");			
			return false;
		}
		
		String correlationId = Objects.toString(inputMap.get("correlationId"), "");
		request.addRequestParam_("X-Correlation-ID", correlationId);
		
		return true;
	}
}
