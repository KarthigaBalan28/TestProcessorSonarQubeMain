package com.hid.onboarding.postprocessor;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class GetCredentialOptionsPostProcessor implements DataPostProcessor2 {
	
	private static final Logger LOG = LogManager.getLogger(com.hid.onboarding.postprocessor.GetCredentialOptionsPostProcessor.class);
	
	private static final String ERROR_MSG_DETAIL_PARAM = "errorMsgDetail";

	@SuppressWarnings("java:S1125")
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception
	{
		LOG.debug("HID : In GetCredentialOptionsPostProcessor");		
		Result res = new Result();
		boolean sequenceFailed = request.getAttribute("sequenceFailed") == null ? false : request.getAttribute("sequenceFailed");
		
		if(sequenceFailed) {
			String errorMsgDetail = request.getAttribute(ERROR_MSG_DETAIL_PARAM) == null ? HIDFabricConstants.SERVICE_FAILURE : request.getAttribute(ERROR_MSG_DETAIL_PARAM);
			res.addErrMsgParam(errorMsgDetail);
			res.addOpstatusParam(-1);
			res.addHttpStatusCodeParam(400);
			return res;
		}

        // Get the integration response from the request instance
		Map <String,String> integrationResponse = response.getHeaders();
        if (integrationResponse != null)
        {
            // Fetch the specific header you are interested in
            String headerName = "server-csrf-token";
            String headerValue = integrationResponse.get(headerName);

            // Add the header value to the result object
            result.addParam(new Param(headerName, headerValue, "String"));
        }
        else
        {
            // Handle the case where the integration response is null
        	result.addOpstatusParam(-1);
			result.addStringParam(ERROR_MSG_DETAIL_PARAM, HIDFabricConstants.SERVICE_FAILURE);
			result.addHttpStatusCodeParam(400);
        }

        return result;
    }

}
