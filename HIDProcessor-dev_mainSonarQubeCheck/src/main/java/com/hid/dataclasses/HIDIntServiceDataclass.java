package com.hid.dataclasses;

public class HIDIntServiceDataclass {
    private String serviceKey;
    private String serviceName;
    private String operationName;
    public HIDIntServiceDataclass(String serviceKey,String serviceName,String operationName) {
        this.serviceKey = serviceKey;
        this.serviceName = serviceName;
        this.operationName = operationName;
    }
	public String getServiceName() {
		return serviceName;
	}
	public String getOperationName() {
		return operationName;
	}    
}
