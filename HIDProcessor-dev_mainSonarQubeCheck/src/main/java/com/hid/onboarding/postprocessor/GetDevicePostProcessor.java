package com.hid.onboarding.postprocessor;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.hid.util.GetConfProperties;
import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetDevicePostProcessor implements DataPostProcessor2 {
	
		public static final Logger LOG = LogManager.getLogger(GetDevicePostProcessor.class);
	
		@Override
		public Object execute(Result result,DataControllerRequest request,DataControllerResponse response)
				throws Exception {
			LOG.debug("HID : In GetDevicePostProcessor");
			
			String opStatus = Objects.toString(result.getOpstatusParamValue(),"");
			String status = Objects.toString(result.getParamValueByName("status"),"");
			
			
			if (!("0".equals(opStatus)) || !("ACTIVE".equals(status))) {
				
				String authKey = Objects.toString(request.getAttribute("auth_key"),"");
				
				request.getServicesManager().getResultCache().removeFromCache(authKey);
				
				LOG.debug("HID GetDevicePostProcessor -->: Cache removed Successfully");
			}
			
			return result;
			
		}
}
