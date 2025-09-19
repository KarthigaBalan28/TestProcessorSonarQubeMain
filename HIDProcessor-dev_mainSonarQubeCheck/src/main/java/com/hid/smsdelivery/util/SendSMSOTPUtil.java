package com.hid.smsdelivery.util;

import org.apache.logging.log4j.Logger;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;

import com.hid.dataclasses.HIDIntServiceDataclass;
import com.hid.smsdelivery.constants.SMSConstants;
import com.konylabs.middleware.api.OperationData;
import com.konylabs.middleware.api.ServiceRequest;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

@SuppressWarnings({"java:S1319", "java:S3008", "java:S1118", "java:S115", "java:S112"})
public class SendSMSOTPUtil {
	
	private static final Logger LOG = LogManager.getLogger(com.hid.smsdelivery.util.SendSMSOTPUtil.class);
	private static final String sendSmsServiceName = "HIDOTPServiceKMSOrch";
	public static Result call(String serivceName, String operationName, DataControllerRequest request,
			HashMap<String, Object> headerMap, HashMap<String, Object> bodyMap) throws Exception {
		OperationData serviceData = request.getServicesManager().getOperationDataBuilder().withServiceId(serivceName)
				.withOperationId(operationName).build();
		ServiceRequest serviceRequest = request.getServicesManager().getRequestBuilder(serviceData).withInputs(bodyMap)
				.withHeaders(headerMap).build();
		return serviceRequest.invokeServiceAndGetResult();
	}

	private static HIDIntServiceDataclass SEND_KMS_SMS = new HIDIntServiceDataclass("SEND_KMS_SMS", "authProductServices",
			"sendKMSSMS");
	
	
	//HID Gateway services
	private static HIDIntServiceDataclass HID_SEND_OTP_GENERIC = new HIDIntServiceDataclass("HID_SEND_OTP_GENERIC",
			"HIDOTPServices", "sendOOB");
	private static HIDIntServiceDataclass HID_SEND_OTP_LOGIN = new HIDIntServiceDataclass("HID_SEND_OTP_LOGIN",
			"HIDOTPServices", "sendOOBLogin");
	private static HIDIntServiceDataclass HID_SEND_OTP_ONBORDING = new HIDIntServiceDataclass("HID_SEND_OTP_ONBORDING",
			"HIDScimApisOrch", "AddOOBAuthAndSendSMS");

	//HID Authservice
	private static HIDIntServiceDataclass HID_SEND_OTP_ONBORDING_KMS = new HIDIntServiceDataclass("HID_SEND_OTP_ONBORDING_KMS",
			sendSmsServiceName, "addOOBAndSendOTPAuthService");
	private static HIDIntServiceDataclass HID_SEND_OTP_LOGIN_KMS = new HIDIntServiceDataclass("HID_SEND_OTP_LOGIN_KMS",
			sendSmsServiceName, "sendLoginOTPAuthService");
	private static HIDIntServiceDataclass HID_SEND_OTP_KMS = new HIDIntServiceDataclass("HID_SEND_OTP_KMS", sendSmsServiceName,
			"generateAndSendOTPAuthService");

	
	//HID Appliance
	private static HIDIntServiceDataclass HID_SEND_OTP_ONBORDING_APP_KMS = new HIDIntServiceDataclass("HID_SEND_OTP_ONBORDING_APP_KMS",
			sendSmsServiceName, "addOOBAndSendOTPAppliance");
	private static HIDIntServiceDataclass HID_SEND_OTP_LOGIN_APP_KMS = new HIDIntServiceDataclass("HID_SEND_OTP_LOGIN_APP_KMS",
			sendSmsServiceName, "sendLoginOTPAppliance");
	private static HIDIntServiceDataclass HID_SEND_OTP_APP_KMS = new HIDIntServiceDataclass("HID_SEND_OTP_APP_KMS", sendSmsServiceName,
			"generateAndSendOTPAppliance");
	
	
	public static HIDIntServiceDataclass getHIDServiceDataObject(String serviceKey) {
		switch (serviceKey) {

		case SMSConstants.SEND_KMS_SMS:
			return SEND_KMS_SMS;

		//HID Gateway services
		case SMSConstants.HID_SEND_OTP_LOGIN:
			return HID_SEND_OTP_LOGIN;
			
		case SMSConstants.HID_SEND_OTP_ONBORDING:
			return HID_SEND_OTP_ONBORDING;
			
		case SMSConstants.HID_SEND_OTP_GENERIC:
			return HID_SEND_OTP_GENERIC;
			
		//HID Auth Service			
		case SMSConstants.HID_SEND_OTP_ONBORDING_KMS:
			return HID_SEND_OTP_ONBORDING_KMS;
			
		case SMSConstants.HID_SEND_OTP_LOGIN_KMS:
			return HID_SEND_OTP_LOGIN_KMS;
			
		case SMSConstants.HID_SEND_OTP_KMS:
			return HID_SEND_OTP_KMS;
			
		//HID Appliance
		case SMSConstants.HID_SEND_OTP_ONBORDING_APP_KMS:
			return HID_SEND_OTP_ONBORDING_APP_KMS;
			
		case SMSConstants.HID_SEND_OTP_LOGIN_APP_KMS:
			return HID_SEND_OTP_LOGIN_APP_KMS;
			
		case SMSConstants.HID_SEND_OTP_APP_KMS:
			return HID_SEND_OTP_APP_KMS;

		default:
			return HID_SEND_OTP_KMS;
		}
	}
}
