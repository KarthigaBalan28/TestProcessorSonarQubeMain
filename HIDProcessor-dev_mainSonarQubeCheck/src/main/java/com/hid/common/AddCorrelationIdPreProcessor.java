package com.hid.common;

import java.util.HashMap;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class AddCorrelationIdPreProcessor implements DataPreProcessor2 {
	
	private static final Logger LOG = LogManager
			.getLogger(com.hid.common.AddCorrelationIdPreProcessor.class);

	@Override
	public boolean execute(
			HashMap inputMap, DataControllerRequest request, DataControllerResponse response, Result result)
					throws Exception
	{
		LOG.debug("HID : In AddCorrelationIdPreProcessor");
		String correlationId = Objects.toString(inputMap.get("correlationId"), "");
		request.addRequestParam_("X-Correlation-ID", correlationId);
		return true;
	}
}