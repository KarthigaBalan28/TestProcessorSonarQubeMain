package com.hid.rmsservices;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.hid.dataclasses.HIDRMSDataclass;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.controller.DataControllerRequest;

@SuppressWarnings({"java:S1319", "java:S1118"})
public class RMSUtils {

    private static HashMap<String,Object> getBaseRequestBody(HIDRMSDataclass hidrms){
    	HashMap<String,Object> rmsBodyMap = new HashMap<String, Object>();
    	rmsBodyMap.put("application_id", hidrms.getApplicationId());
		rmsBodyMap.put("channel_id", hidrms.getChannelID());
		rmsBodyMap.put("environment_id",hidrms.getEnvironmentID());
		return rmsBodyMap;
    }
    public static HashMap<String,Object> getLoginRequestBody(HIDRMSDataclass hidrms, String username, String loginStepResult, Integer loginFactorIndex,String appActionId){
    	HashMap<String, Object> rmsBodyMap  = getBaseRequestBody(hidrms);
		rmsBodyMap.put("tm_device_tag", hidrms.getTmDeviceTag());
		rmsBodyMap.put("tm_session_sid", hidrms.getTmSessionId());
		rmsBodyMap.put("app_user_id", username);
		rmsBodyMap.put("app_action_id", appActionId);
		rmsBodyMap.put("login_step_result", loginStepResult);
		rmsBodyMap.put("login_factor_index", loginFactorIndex);
		rmsBodyMap.put("security_item_id", hidrms.getSecurityItemId());
		rmsBodyMap.put("security_item_type", hidrms.getSecurityItemType());
		rmsBodyMap.put("app_session_id", hidrms.getAppSessionId());
		rmsBodyMap.put("client_ip", hidrms.getClientIp());
		return rmsBodyMap;
    }
    public static HashMap<String,Object> getLoginSuccessRequestBody(HIDRMSDataclass hidrms, String username, String tmActionId,String appSessionId){
    	HashMap<String, Object> rmsBodyMap  = getBaseRequestBody(hidrms);
    	rmsBodyMap.put("application_id", hidrms.getApplicationId());
		rmsBodyMap.put("channel_id", hidrms.getChannelID());
		rmsBodyMap.put("tm_device_tag", hidrms.getTmDeviceTag());
		rmsBodyMap.put("tm_session_sid", hidrms.getTmSessionId());
		rmsBodyMap.put("app_user_id", username);
		rmsBodyMap.put("app_session_id", appSessionId);
		rmsBodyMap.put("client_ip", hidrms.getClientIp());
		rmsBodyMap.put("tm_action_id", tmActionId);
		return rmsBodyMap;
    }
    
    public static String[] getSignalTags() {
    	return new String[] {"new_device","new_country","new_ip"};
    }
    public static String getApplicationId(DataControllerRequest request, String platform) {
    	String applicationId = "";
    	try {
    		if(platform.equalsIgnoreCase("android")) {
    			applicationId = GetConfProperties.getProperty(request, AuthenticationConstants.HID_RMS_APPLICATION_ID_ANDROID_KEY);
    		}else if(platform.equalsIgnoreCase("ios")){
    			applicationId = GetConfProperties.getProperty(request, AuthenticationConstants.HID_RMS_APPLICATION_ID_IOS_KEY);
    		}else
    			applicationId = GetConfProperties.getProperty(request, AuthenticationConstants.HID_RMS_APPLICATION_ID_KEY);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return applicationId;
    }
    public static String getChannelId(DataControllerRequest request, String platform) {
    	String channelId = "";
    	try {
    		if(platform.equalsIgnoreCase("android")) {
    			channelId = GetConfProperties.getProperty(request, AuthenticationConstants.HID_RMS_MOBILE_CHANNEL_ID_KEY);
    		}else if(platform.equalsIgnoreCase("ios")){
    			channelId = GetConfProperties.getProperty(request, AuthenticationConstants.HID_RMS_MOBILE_CHANNEL_ID_KEY);
    		}else
    			channelId = GetConfProperties.getProperty(request, AuthenticationConstants.HID_RMS_CHANNEL_ID_KEY);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return channelId;
    }
    
    public static String getClientIp(DataControllerRequest request) {
    	 String urls = request.getHeader("X-Forwarded-For");
    	 if(StringUtils.isEmpty(urls)) return ""; 
    	 String[] clientUrls = urls.split(",");
    	 return clientUrls[0];
    }
}
