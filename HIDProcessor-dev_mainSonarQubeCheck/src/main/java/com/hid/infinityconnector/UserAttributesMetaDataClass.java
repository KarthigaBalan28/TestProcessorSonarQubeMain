package com.hid.infinityconnector;

import java.util.HashMap;
@SuppressWarnings({ "java:S115", "java:S2293", "java:S1319" })
public class UserAttributesMetaDataClass {
     private  String serviceName = "dbpProductServices";
     private  String operationName = "getCustomerIdentityAttributes";
     private  String userNameParam  = "userName";
     public  static final String securtiyAttParamName = "security_attributes";
     public  static final String userAttParamName = "user_attributes";
	 public static final String ERROR_MESSAGE = "errmsg";
	 public static final String DBP_ERROR_CODE_KEY = "dbpErrCode";
	 public static final String DBP_ERROR_MESSAGE_KEY = "dbpErrMsg";
    
     public String getServiceName() {
    	 return serviceName;
     }
     
     public String getOperationName(){
    	 return operationName;
     }
     
     public HashMap<String, Object> formRequestMap(String username){
    	 HashMap<String, Object> bodyMap = new HashMap<String , Object>();
    	 bodyMap.put(userNameParam, username);
    	 return bodyMap;
     }
     
     public HashMap<String, Object> formHeaderMap(){
    	 return new HashMap<String , Object>(); 
     }
}
