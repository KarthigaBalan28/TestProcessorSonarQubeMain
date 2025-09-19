package com.hid.identity.thirdparty;

import java.util.HashMap;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

@SuppressWarnings({"java:S5993", "java:S3740", "java:S1319"})
public abstract class ThirdPartyServiceMeta {
	    private String serviceName;
	    private String operationName;
	    public ThirdPartyServiceMeta(String serviceName, String operationName) {
			this.serviceName = serviceName;
			this.operationName = operationName;
		}
		public String getServiceName() {
			return serviceName;
		}
		public String getOperationName() {
			return operationName;
		}
		public abstract HashMap<String, Object> formRequestMap(HashMap inputMap,DataControllerRequest request);
		public abstract HashMap<String, Object> formHeaderMap(HashMap inputMap ,DataControllerRequest request);
		public abstract void validateResult(Result result);
}
