package com.hid.onboarding.postprocessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetAuthenticatedTokenPostProcessor implements DataPostProcessor2 {
	
	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.postprocessor.GetAuthenticatedTokenPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {

		LOG.debug("HID : In GetAuthenticatedTokenPostProcessor");
		
		LOG.debug("HID : In GetAuthenticatedTokenPostProcessor response: {}", response.getResponse());
        LOG.debug("HID : In GetAuthenticatedTokenPostProcessor result: {}", result.getAllParams());
        
        String responseString = response.getResponse();
        
        String code="";
        String context ="";
        
        try {
            JSONObject responseJson = new JSONObject(responseString);
            code = responseJson.getString("code");
            context = responseJson.getString("context");
        }
        catch(JSONException e) {
            String errMsg = "Auth Code / Context were not available in the response.\n"
                    + "Error Message in response(if any): " 
                    + responseString;
            
            result.addErrMsgParam(errMsg);
            return result;
        }
        
		if(!context.isEmpty() || !code.isEmpty()) {
			result.addStringParam("context", context);
			result.addStringParam("code", code);
		} else {
			result.addOpstatusParam(-1);
			result.addStringParam("GetAuthenticatedTokenServiceError", "id_token or code is missing");
			result.addErrMsgParam("Invalid response from service");

		}		
		return result;
	}

}
