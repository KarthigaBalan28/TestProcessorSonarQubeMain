package com.hid.onboarding.preprocessor;

import java.util.HashMap;
import java.util.Objects;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class AddTXOOBPreProcessor implements DataPreProcessor2 {
	private static final Logger LOG = LogManager.getLogger(com.hid.onboarding.preprocessor.AddTXOOBPreProcessor.class);

	private static final String SEQUENCE_FAILED_PARAM = "sequenceFailed";
	
	@SuppressWarnings({"java:S1854","java:S1125","java:S1481"})
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {

		LOG.debug("HID : In AddTXOOBPreProcessor");
		String username = Objects.toString(inputMap.get("username"), "");
		boolean sequenceFailed = request.getAttribute(SEQUENCE_FAILED_PARAM) == null ? false
				: request.getAttribute(SEQUENCE_FAILED_PARAM);

		if (sequenceFailed) {
			result.addOpstatusParam(-1);
			result.addErrMsgParam("Sequence Failed");
			request.setAttribute(SEQUENCE_FAILED_PARAM, true);
			request.setAttribute("errorMsgDetail", request.getAttribute("errorMsgDetail"));
			return false;
		}

		request.setAttribute(SEQUENCE_FAILED_PARAM, false);
		return true;

	}

}
