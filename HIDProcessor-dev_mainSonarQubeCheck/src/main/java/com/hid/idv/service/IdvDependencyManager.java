package com.hid.idv.service;

import com.hid.idv.utils.IDVConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.controller.DataControllerRequest;

@SuppressWarnings("java:S1118")
 public class IdvDependencyManager {

	public static IdvCachingService getCacheService(DataControllerRequest request) {
		return new IdvMiddlewareCache(request);
	}

	public static IdvTokenService getTokenService(DataControllerRequest request) {
		String isCacheEnabled = "";
		try {
		   isCacheEnabled = GetConfProperties.getProperty(request, IDVConstants.IDV_IS_CACHING_KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 if(isCacheEnabled.isEmpty() || isCacheEnabled.equalsIgnoreCase("false")) {
			 return new TokenServiceWOCache(request);
		 }
		 return new TokenServiceWithCache(request);
	}
	

}
