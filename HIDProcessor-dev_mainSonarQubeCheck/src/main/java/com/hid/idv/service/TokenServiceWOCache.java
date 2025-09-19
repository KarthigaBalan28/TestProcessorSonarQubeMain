package com.hid.idv.service;

import java.util.HashMap;

import com.hid.idv.utils.IDVConstants;
import com.hid.util.HIDIntegrationServices;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

 class TokenServiceWOCache implements IdvTokenService {

    private DataControllerRequest request;
    
    public TokenServiceWOCache(DataControllerRequest request) {
    	 this.request = request;
    }
    
	@Override
	public String getToken() {
	   Result result = null;
	   try {
	   result = HIDIntegrationServices.call(IDVConstants.GETTOKEN_SERVICE_NAME, IDVConstants.GETTOKEN_OPR_NAME, request, new HashMap<String, Object>(), new HashMap<String, Object>());
	} catch (Exception e) {
		e.printStackTrace();
		return "";
	}
	   if(result == null || !result.getOpstatusParamValue().contentEquals(IDVConstants.SUCCESS_OPSTATUS)){
		   return "";
	   }
	   return result.getParamValueByName(IDVConstants.GETTOKEN_TOKEN_PARAM);
	}

}
