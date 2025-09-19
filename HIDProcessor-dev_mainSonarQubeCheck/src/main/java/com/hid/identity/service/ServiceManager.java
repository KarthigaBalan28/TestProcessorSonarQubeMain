package com.hid.identity.service;

import java.util.HashMap;
import java.util.Objects;

import com.hid.identity.factory.HIDIdentityServiceFactory;
import com.hid.identity.factory.IdentityServiceFactory;
import com.hid.identity.factory.ThirdPartyIdentityServiceFactory;
import com.konylabs.middleware.controller.DataControllerRequest;

@SuppressWarnings({"java:S1118" , "java:S3740" , "java:S1172" , "java:S1319"})
public class ServiceManager {
     public static IdentityServiceFactory getIdentityFactory(HashMap inputMap, DataControllerRequest request) {
    	 String authType = Objects.toString(inputMap.get("authType"), "");
    	 if(authType.isEmpty()) return new ThirdPartyIdentityServiceFactory();
    	 return new HIDIdentityServiceFactory();
     }
}
