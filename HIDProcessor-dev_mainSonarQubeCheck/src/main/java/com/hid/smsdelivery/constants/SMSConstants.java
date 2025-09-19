package com.hid.smsdelivery.constants;

@SuppressWarnings("java:S1118")
public class SMSConstants {
	public static final String GENERIC_ERROR = "Failed to send the OTP! please try again";
	public static final String PHONE_NUM_PARAM = "phoneNo";
	public static final String OTP_PARAM = "otp";
	public static final String OTP_SUCCESS_PARAM = "OOB_SENT";
	public static final String AUTH_TYPE_PARAM = "authType";
    public static final String HID_IS_GATEWAY_ENABLED = "HID_IS_GATEWAY_ENABLED";
	public static final String SEND_KMS_SMS = "SEND_KMS_SMS";
	public static final String SERVICE_FAILURE = "Failed to send the OTP! please try again";
	public static final String INVALID_AUTHENTICATOR = "The authenticator status is invalid";
	public static final String OOB_GENERATION_FAILURE = "The OOB secret generation has failed";
	public static final String MAXIMUM_THRESHOLD = "The Authenticator has reached its threshold";
	public static final String SEARCH_USER_FAILED = "Failed to fetch the user details";
	//HID Gateway defualt services
	public static final String HID_SEND_OTP_ONBORDING = "HID_SEND_OTP_ONBORDING";
	public static final String HID_SEND_OTP_GENERIC = "HID_SEND_OTP_GENERIC";
	public static final String HID_SEND_OTP_LOGIN =  "HID_SEND_OTP_LOGIN";
	//HID Auth services
	public static final String HID_SEND_OTP_ONBORDING_KMS  = "HID_SEND_OTP_ONBORDING_KMS";
	public static final String HID_SEND_OTP_LOGIN_KMS  = "HID_SEND_OTP_LOGIN_KMS";
	public static final String HID_SEND_OTP_KMS = "HID_SEND_OTP_KMS";
	//HID Appliance services
	public static final String HID_SEND_OTP_LOGIN_APP_KMS = "HID_SEND_OTP_LOGIN_APP_KMS";
	public static final String HID_SEND_OTP_APP_KMS = "HID_SEND_OTP_APP_KMS";
	public static final String HID_SEND_OTP_ONBORDING_APP_KMS = "HID_SEND_OTP_ONBORDING_APP_KMS";
	
	public static final String PROPERTY_NOT_FOUND = "HID_IS_GATEWAY_ENABLED property is missing in Fabric server property";
	public static final String HID_APPLIANCE_ENV_KEY = "HID_IS_APPLIANCE";
	

}
