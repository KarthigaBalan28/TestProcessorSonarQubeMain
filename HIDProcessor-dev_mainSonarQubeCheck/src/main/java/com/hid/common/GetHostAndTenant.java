package com.hid.common;

import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.hid.util.AuthenticationConstants;
import com.hid.util.HIDFabricConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;


public class GetHostAndTenant implements DataPreProcessor2 {

	private static final Logger LOG = LogManager.getLogger(com.hid.common.GetHostAndTenant.class);
	
	@SuppressWarnings("java:S3516")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request,
	DataControllerResponse response, Result result) throws Exception 
	{
		try {
			LOG.debug("HID : In GetHostAndTenant");
			String host = GetConfProperties.getProperty(request, AuthenticationConstants.HID_HOST_KEY);
			LOG.debug("HID : Value of HOST from server settings is : {}", host);
			if (host.isEmpty()) {
				LOG.debug("HID : There is no HOST key present in server properties, returning false");
				setErrorToResult(result, HIDFabricConstants.HOST_NOT_EXIST, -1, 404);
				return false;
			}
			String tenant = GetConfProperties.getProperty(request, AuthenticationConstants.HID_TENANT_KEY);
			LOG.debug("HID : Value of HID_TENANT_KEY from server settings is : {}", tenant);
			if (tenant.isEmpty()) {
				LOG.debug("HID : There is no TENANT key present in server properties, returning false");
				setErrorToResult(result, HIDFabricConstants.TENANT_NOT_EXIST, -1, 404);
				return false;
			}
			result.addOpstatusParam(0);
			result.addHttpStatusCodeParam(200);
			result.addStringParam("success", "true");
			result.addStringParam("host", host);
			result.addStringParam("tenant", tenant);
		    return false;
			
		} catch (Exception e) {		
			setErrorToResult(result, e.getMessage(), -2, 403);
			LOG.error("HID::GetHostAndTenant ---> Exception occured while calling GetHostAndTenant Service {}", e.getMessage());
			e.printStackTrace();
		}
		return false;
	}		
	private void setErrorToResult(Result result, String errmsg, int opstatus, int code) {
	  result.addOpstatusParam(opstatus);
	  result.addErrMsgParam(errmsg);
	  result.addHttpStatusCodeParam(code);
	  result.addStringParam("errMsg", errmsg);
    }
 }
    
