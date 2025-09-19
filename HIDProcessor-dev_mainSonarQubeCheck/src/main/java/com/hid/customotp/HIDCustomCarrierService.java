package com.hid.customotp;

import com.hid.util.HIDIntegrationServices;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public class HIDCustomCarrierService implements SMSCarrierService {

	@Override
	public Result sendSMS(DataControllerRequest request) {
		SMSServiceMetaData metaData = DependencyManager.getServiceMetaData();
		Result result = callService(metaData, request);
		metaData.validateResult(result);
		return result;
	}
	
	public Result callService(SMSServiceMetaData metaData,DataControllerRequest request){
		try {
			return HIDIntegrationServices.call(
					metaData.getServiceName(), 
					metaData.getOperationName(),
					request,
					metaData.formHeaderMap(request),
					metaData.formRequestMap(request)
                   );
		} catch (Exception e) {
			return null;
		}
	}

}
