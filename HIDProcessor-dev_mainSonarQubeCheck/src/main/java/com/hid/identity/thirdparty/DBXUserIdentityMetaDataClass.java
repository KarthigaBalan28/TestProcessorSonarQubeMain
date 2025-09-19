package com.hid.identity.thirdparty;

import java.util.HashMap;
import java.util.Map;

import com.hid.identity.util.IdentityLogger;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public class DBXUserIdentityMetaDataClass extends ThirdPartyServiceMeta{
	public DBXUserIdentityMetaDataClass() {
		super("DbxCustomerLogin", "login");
	}

	@SuppressWarnings({"java:S2864" , "java:S1643"})
	@Override
	public HashMap<String, Object> formRequestMap(HashMap inputMap, DataControllerRequest request) {
		HashMap<String, Object> bodyMap = new HashMap<>();
		String keysForLog = "";
		for(Object key: inputMap.keySet()) {
			if(key instanceof String) {
				bodyMap.put((String) key, inputMap.get(key));
				keysForLog += "|"+ (String)key;
			}
		}
		log("Parameters passed to Body of DBXIdentity : " + keysForLog.substring(1));
		return bodyMap;
	}

	@SuppressWarnings({"java:S2864" , "java:S1643"})
	@Override
	public HashMap<String, Object> formHeaderMap(HashMap inputMap, DataControllerRequest request) {
		HashMap<String, Object> headerMap = new HashMap<>();
		Map<String, Object> reqMap = request.getHeaderMap();
		String keysForLog = "";
		for(String key : reqMap.keySet()) {
			headerMap.put(key, reqMap.get(key));
			keysForLog += "|"+ key;
		}
		log("Parameters passed to headers of DBXIdentity : " + keysForLog.substring(1));
		return headerMap;
	}

	@Override
	public void validateResult(Result result) {
		//do Nothing
		
	}
	
	private void log(String msg) {
		 IdentityLogger.debug("ThirdPartyService", this.getClass().getSimpleName(), msg);
	}

     
}
