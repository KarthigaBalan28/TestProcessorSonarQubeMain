package com.hid.authentication.postprocessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONException;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetAuthCodePostProcessor implements DataPostProcessor2 {
    
    private static final Logger LOG = LogManager.getLogger(GetAuthCodePostProcessor.class);

    @Override
    public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
            throws Exception
    {
        String responseString = response.getResponse();
        if (LOG.isDebugEnabled()) {
            LOG.debug("GetAuthCodePostProcessor - Response: {}", responseString);
            LOG.debug("GetAuthCodePostProcessor - Result - Params: {}", result.getAllParams());
        }
        
        String code;
        String context;
        
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
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("GetAuthCodePostProcessor - Response - Code: {}", code);
            LOG.debug("GetAuthCodePostProcessor - Response - Context: {}", context);
        }        
        
        result.addStringParam("code", code);
        result.addStringParam("context", context);
            
        if (LOG.isDebugEnabled()) {
            LOG.debug("GetAuthCodePostProcessor - Result - Records: {}", result.getAllRecords());
            LOG.debug("GetAuthCodePostProcessor - Result - Datasets: {}", result.getAllDatasets());
        }
        
        return result;
    }

}
