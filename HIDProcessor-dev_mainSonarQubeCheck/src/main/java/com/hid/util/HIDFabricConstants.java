package com.hid.util;
@SuppressWarnings({"java:S1118"})
public class HIDFabricConstants {
    public static final String AUTH_USED = "Activation Code already used";
    public static final String THRESHOLD = "Failure attempts threshold reached please contact bank";
    public static final String AUTH_NOT_EXIST = "User is not eligible for Onboarding please contact bank";
    public static final String USER_NOT_EXIST = "Given Username does not exist please Check Username";
    public static final String EXPIRED = "Activation Code Expired please contact bank";
    public static final String SERVICE_FAILURE = "Service Failed Please try again Later";
    public static final Long YEARS = 2L;
    public static final Long DEFAULT_PWD_EXPIRY_DAYS = 365L;
    public static final Long DEFAULT_ACT_EXPIRY_DAYS = 1L;
    public static final String PWD_EXPIRY_TIME = "HID_PWD_EXPIRY_IN_DAYS";
    public static final String ACT_EXPIRY_TIME = "HID_ACT_EXPIRY_IN_DAYS";
    public static final String OFFSET_TIME = "+00:00";
    public static final String PWD_EXISTS = "Password Authenticator already exists";
    public static final String ORG_DETAILS_NOT_EXIST = "Org Credentails are not present in server properties";
    public static final String TOKEN_FAILED = "Failed to Fetch Backend token";
    public static final String APP_DETAILS_NOT_EXIST = "App Key and App secret are not configured in server properties";
    public static final String INCORRECT_ACTIVATION_CODE = "Provided activation code is incorrect, please enter the valid Activation code";
    public static final String USER_DEFAULT_PWD = "HID_USER_DEFAULT_PWD";
    public static final String DEVICES_NOT_FOUND = "No devices returned from search";
    public static final String ATTRIBUTE_PARSING_ERROR = "Error while parsing the attributes";
    public static final String MOMBILENO_NOT_EXIST = "mobileNumber feild is empty";
    public static final String HID_APPLIANCE_ENV_KEY = "HID_IS_APPLIANCE";
    public static final String HID_APPLIANCE_ENV_VALUE = "true";
    public static final String HOST_NOT_EXIST = "HID_HOST value not present in server properties";
    public static final String TENANT_NOT_EXIST ="HID_TENANT value not present in server properties";
    public static final String AUTH_SMS_OOB_FAILURE = "AddOOBAuthenticator service failed";
    public static final String AUTH_TX_OOB_FAILURE = "AddTXOOBAuthenticator service failed";
    public static final String AUTH_SEND_OTP_FAILURE = "Failed to send the OTP";
    public static final String HID_SMS_OOB_FRIENDLY_NAME = "SMS Login Device";
    public static final String HID_TX_OOB_FRIENDLY_NAME = "SMS Transaction Device";
    public static final String HID_CACHE_EXPIRY_TIME_IN_SECONDS = "HID_CACHE_EXPIRY_TIME";
}
