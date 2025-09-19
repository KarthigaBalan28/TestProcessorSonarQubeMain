package com.hid.idv.service;

import com.hid.idv.utils.IDVConstants;
import com.konylabs.middleware.controller.DataControllerRequest;

public class TokenServiceWithCache implements IdvTokenService {
    
	DataControllerRequest request;
	IdvCachingService cachingService;
	TokenServiceWithCache(DataControllerRequest request){
		this.request = request;
		cachingService = IdvDependencyManager.getCacheService(request);
	}
	
	@Override
	public String getToken() {
		String key = IDVConstants.CACHE_KEY;
		String value = cachingService.retrieveFromCache(key);
		if(!value.isEmpty()) return value;
		value = new TokenServiceWOCache(request).getToken();
		if(!value.isEmpty()) {
		   cachingService.addToCache(key, value, IDVConstants.CACHE_TIMEOUT);
		}
		return value;
	}

}
