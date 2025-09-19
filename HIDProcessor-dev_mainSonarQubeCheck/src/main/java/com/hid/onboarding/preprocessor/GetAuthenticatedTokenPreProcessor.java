package com.hid.onboarding.preprocessor;

import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hid.common.ClientBasePreprocessor;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetAuthenticatedTokenPreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {
	
	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.preprocessor.GetAuthenticatedTokenPreProcessor.class);
	
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
			LOG.debug("HID : In GetAuthenticatedTokenPreProcessor");
			if (super.execute(inputMap, request, response, result)) {
			String serverCsrfToken = Objects.toString(request.getAttribute("csrfx")) == null ? ""
					: Objects.toString(request.getAttribute("csrfx"));

			if(!StringUtils.isEmpty(serverCsrfToken)) {
				request.getHeaderMap().put("server-csrf-token", serverCsrfToken);
			}else {
				result.addOpstatusParam(-1);
				result.addStringParam("GetAuthenticatedTokenServiceError", "server-csrf-token is missing");
				result.addHttpStatusCodeParam(400);
				result.addErrMsgParam("Invalid header payload");
				return false;
			}
			return true;
		}
		return false;
	}
}

