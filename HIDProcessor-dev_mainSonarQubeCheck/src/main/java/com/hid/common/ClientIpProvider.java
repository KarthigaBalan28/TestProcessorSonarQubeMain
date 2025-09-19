package com.hid.common;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.hid.rmsservices.RMSUtils;

public class ClientIpProvider implements DataPreProcessor2 {
	private static final Logger LOG = LogManager.getLogger(com.hid.common.ClientIpProvider.class);
	
	@SuppressWarnings("java:S3516")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request,DataControllerResponse response, Result result) throws Exception 
	{
		try {
			String clientIp = RMSUtils.getClientIp(request);
			LOG.debug("HID : Value of ClientIp is : {}", clientIp);
			if (clientIp.equals("")) {
				String message = "Client Ip not found";
				setErrorToResult(result, message, -2, 404);
				return false;
			}
			result.addOpstatusParam(0);
			result.addHttpStatusCodeParam(200);
			result.addStringParam("success", "true");
			result.addStringParam("clientIp", clientIp);
			return false;
			
		} catch (Exception e) {
			setErrorToResult(result, e.getMessage(), -2, 403);
			LOG.error("HID::GetClientIp ---> Exception occured while calling GetClientIp Service {}", e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	private void setErrorToResult(Result result, String errmsg, int opstatus, int code) {
		result.addOpstatusParam(opstatus);
		result.addHttpStatusCodeParam(code);
		result.addErrMsgParam(errmsg);
		result.addStringParam("errMsg", errmsg);
		    }
	}

