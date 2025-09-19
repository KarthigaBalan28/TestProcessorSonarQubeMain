package com.hid.customotp;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hid.util.GetConfProperties;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

@SuppressWarnings("java:S1854","java:S1185")
public class CustomSendOTPServiceMeta extends SMSServiceMetaData {
    CustomSendOTPServiceMeta(){
    	super("authProductServices", "sendKMSSMS");
    }
    private static final Logger LOG = LogManager
			.getLogger(com.hid.customotp.CustomSendOTPServiceMeta.class);

	@Override
	public HashMap<String, Object> formRequestMap(DataControllerRequest request) {
		String phoneNo = request.getAttribute("phoneNo");
		String otp = request.getAttribute("otp");
		HashMap<String , Object> bodyMap = new HashMap<>();
	    bodyMap.put("sendToMobiles", phoneNo);
	    bodyMap.put("smsText", formMessage(request));
		return bodyMap;
	}

	@Override
	public HashMap<String, Object> formHeaderMap(DataControllerRequest request) {
		return new HashMap<>();
	}
	
	@Override
	public void validateResult(Result result){
		if(result == null) return;
		String msg = result.getParamValueByName("success");
		if(StringUtils.isEmpty(msg)) {
			result.addOpstatusParam(-1);
			result.addParam("custErr", "Unable to send OTP, Please try again");
		}
	}
	
	private String formMessage(DataControllerRequest request){
		String otp = request.getAttribute("otp");
		String defaultStr = "Dear customer otp is #";
	    String custTemp;
		try {
			custTemp = GetConfProperties.getProperty(request, "HID_LOGIN_SMS_MSG_TEMPLATE");
		} catch (Exception e) {
			custTemp = "";
		}
	    if(!StringUtils.isEmpty(custTemp) && custTemp.indexOf("#") != -1) {
	    	defaultStr = custTemp;
	    }
		return defaultStr.replace("#", otp);
	}

}
