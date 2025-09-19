package com.hid.idv.service;

import java.util.Objects;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.registry.AppRegistryException;

 class IdvMiddlewareCache implements IdvCachingService{
    DataControllerRequest request;
    
    IdvMiddlewareCache(DataControllerRequest request){
      this.request = request;
    }
    
	@Override
	public void addToCache(String key, String value, int time) {
		try {
			request.getServicesManager().getResultCache().insertIntoCache(key, value, time);
		} catch (AppRegistryException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public String retrieveFromCache(String key) {
		try {
			return Objects.toString(request.getServicesManager().getResultCache().retrieveFromCache(key), "");
		} catch (AppRegistryException e) {
			return "";
		}
	}

	@Override
	public void removeFromCache(String key) {
		try {
			request.getServicesManager().getResultCache().removeFromCache(key);
		} catch (AppRegistryException e) {
			e.printStackTrace();
		}
	}

}
