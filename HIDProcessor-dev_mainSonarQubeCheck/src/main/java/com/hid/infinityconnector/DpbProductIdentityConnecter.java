package com.hid.infinityconnector;

import com.hid.util.HIDIntegrationServices;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public class DpbProductIdentityConnecter implements UserAttributesService{

	@Override
	public Result getUserAttributes(String username, DataControllerRequest request) {
		UserAttributesMetaDataClass usrAttrDataClass = new UserAttributesMetaDataClass();
		try {
			return HIDIntegrationServices.call(
					usrAttrDataClass.getServiceName(), 
					usrAttrDataClass.getOperationName(),
					request,
					usrAttrDataClass.formHeaderMap(),
					usrAttrDataClass.formRequestMap(username)
                   );
		} catch (Exception e) {
			return null;
		}
		
	}

}
