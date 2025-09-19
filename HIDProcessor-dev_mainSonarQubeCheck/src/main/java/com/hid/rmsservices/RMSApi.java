package com.hid.rmsservices;

import com.hid.dataclasses.HIDRMSDataclass;
import com.konylabs.middleware.controller.DataControllerRequest;

public interface RMSApi {
	
	public String getTag(HIDRMSDataclass hidrms, String username, DataControllerRequest request,
			String loginStepResult);

}
