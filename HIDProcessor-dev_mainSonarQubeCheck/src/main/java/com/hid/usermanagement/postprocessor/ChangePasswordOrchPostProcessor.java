package com.hid.usermanagement.postprocessor;

import org.apache.logging.log4j.Logger; 
import org.apache.logging.log4j.LogManager;

import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
@SuppressWarnings({"java:S1125"})
public class ChangePasswordOrchPostProcessor implements DataPostProcessor2 {
	private static final Logger LOG = LogManager.getLogger(com.hid.usermanagement.postprocessor.ChangePasswordOrchPostProcessor.class);
	
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)  throws Exception{
		LOG.debug("HID : In ChangePasswordOrchPostProcessor");
		boolean sequenceFailed = request.getAttribute("sequenceFailed") == null ? false : request.getAttribute("sequenceFailed");
		if(sequenceFailed) {
			String errorMsgDetail = request.getAttribute("errorMsgDetail") == null ? HIDFabricConstants.SERVICE_FAILURE : request.getAttribute("errorMsgDetail");			
			result.addStringParam("ChangePasswordError", errorMsgDetail);
			result.addStringParam("status", "false");
			result.addOpstatusParam(-1);
		}
		return result;
	}

}
