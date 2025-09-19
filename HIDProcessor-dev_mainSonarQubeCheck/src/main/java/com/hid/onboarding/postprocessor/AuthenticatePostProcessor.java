package com.hid.onboarding.postprocessor;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class AuthenticatePostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.postprocessor.AuthenticatePostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {

		LOG.debug("HID : In AuthenticatePostProcessor");
		Result res = new Result();
		
		// Get the integration response from the request instance
		Map <String,String> integrationResponse = response.getHeaders();
        if (integrationResponse != null) {
            // Fetch the specific header you are interested in
            String headerName = "server-csrf-token";
            String headerValue = integrationResponse.get(headerName);
            // Add the header value to the result object
            request.setAttribute("csrfx", headerValue);
            String errmsg = result.getParamValueByName("errmsg")== null? "" : result.getParamValueByName("errmsg");
    		
    		if(!errmsg.isEmpty() && errmsg.equalsIgnoreCase("empty response received")) {
    			res.addOpstatusParam(0);
    			res.addHttpStatusCodeParam(200);
    			res.addParam("success", "true");
    			return res;
    		}
        }else {
			res.addOpstatusParam(-1);
			res.addStringParam("AuthenticateServiceError", "header is missing");
			res.addErrMsgParam("Invalid header payload");
			return res;
		}		
		return result;
	}

}

