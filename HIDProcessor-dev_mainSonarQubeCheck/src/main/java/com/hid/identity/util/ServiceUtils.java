package com.hid.identity.util;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.registry.AppRegistryException;

@SuppressWarnings({"java:S1118"})
public class ServiceUtils {
    public static final  void setErrorToResult(Result result, String errmsg, int opstatus, int code) {
		result.addOpstatusParam(opstatus);
		result.addErrMsgParam(errmsg);
		result.addHttpStatusCodeParam(code);
		result.addStringParam("errMsg", errmsg);
	}
    
    public static final void removeFromCache(DataControllerRequest request, String key) {
    	try {
			request.getServicesManager().getResultCache().removeFromCache(key);
		} catch (AppRegistryException e) {
			e.printStackTrace();
		}
    }

	public static void insertIntoCache(DataControllerRequest request, String key, String value,
			int identityTimer) {
		try {
			request.getServicesManager().getResultCache().insertIntoCache(key, value,
					identityTimer);
		} catch (AppRegistryException e) {
			e.printStackTrace();
		}
		
	}
}  
