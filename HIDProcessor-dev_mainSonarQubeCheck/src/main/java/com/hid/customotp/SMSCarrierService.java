package com.hid.customotp;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public interface SMSCarrierService {
	 Result sendSMS(DataControllerRequest request);
}
