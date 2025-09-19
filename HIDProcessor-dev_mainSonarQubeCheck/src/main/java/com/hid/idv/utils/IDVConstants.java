package com.hid.idv.utils;

@SuppressWarnings("java:S1118")
public class IDVConstants {

    public static final String IDV_USERNAME_KEY = "HID_IDV_USERNAME";
    public static final String IDV_PASSWORD_KEY = "HID_IDV_PASSWORD";
	public static final String IDV_CONFIG_MISSING = "Mandatory IDV configuration settings are missing. Please configure them on Fabric Settings";
	public static final Object GETTOKEN_USERNAME_PARAM = "username";
	public static final Object GETTOKEN_PASSWORD_PARAM = "password";
	public static final String GETTOKEN_SERVICE_NAME = "HID_IDV_Service";
	public static final String GETTOKEN_OPR_NAME = "getToken";
	public static final String GETTOKEN_TOKEN_PARAM = "token";
	public static final String SUCCESS_OPSTATUS = "0";
	public static final String CACHE_KEY = "IDV_TOKEN";	
	public static final int CACHE_TIMEOUT = 10;
	public static final String IDV_IS_CACHING_KEY = "HID_IDV_TOKEN_CACHING_ENABLED";
	public static final String ERROR_TOKEN_INVALID = "IDV Token is invalid";
	public static final String REQ_STATUS_PENDING = "INPROGRESS";
	public static final String ERROR_PENDING_STATUS = "Document submission is still in progress";
	public static final String REQ_STATUS_FAILED = "FAILED";
	public static final String REQ_STATUS_EXPIRED = "EXPIRED";
	public static final String ERROR_STATUS_FAILED = "Document Submission failed please try again";
	public static final String ERROR_STATUS_EXPIRED = "Document Submission link is expired please request again for verification";
	public static final String IDV_ACCOUNT_ACCESS_KEY = "HID_IDV_ACCOUNT_ACCESS_KEY";
	public static final String IDV_SECRET_TOKEN_KEY = "HID_IDV_SECRET_TOKEN";
	public static final String ACCOUNT_KEY_PARAM = "AccountAccessKey";
	public static final String SECRET_TOKEN_PARAM = "SecretToken";
	public static final String STATUS_LOOPING_PARAM = "LoopDataset";
	public static final String REQ_STATUS_SUCCESS = "SUCCESS";
	public static final String IDV_MISSING_ERROR_MESSAGE = "Missing uid in the request";
	//IDV Status UID Parameters
	public static final String TRANSACTIONID_PARAM_NAME = "transactionId";
	public static final String REQ_STATUS_PARAM_NAME = "requestStatus";
	public static final String IMAGE_FRONT_PARAM_NAME = "imageFront";
	public static final String IMAGE_SELFIE_PARAM_NAME = "imageSelfie";
	public static final String SURNAME_PARAM_NAME = "Surname";
	public static final String FIRSTNAME_PARAM_NAME = "FirstName";
	public static final String GIVENNAME_PARAM_NAME = "GivenName";
	public static final String ISSUERNAME_PARAM_NAME = "IssuerName";
	public static final String BIRTHDATE_PARAM_NAME = "BirthDate";
	public static final String ACTIONMESSAGE_PARAM_NAME = "actionMessage";
	public static final String TRANSCATIONSTATUS_PARAM_NAME = "transactionStatus";
	public static final String CODE_PARAM_NAME = "code";
	public static final String SEX_PARAM_NAME = "Sex";
	public static final String ISSUEDATE_PARAM_NAME = "IssueDate";
	public static final String DOCUMENTNUMBER_PARAM_NAME = "DocumentNumber";
	public static final String DOCUMENTISSUERCOUNTRY_PARAM_NAME = "documentIssuerCountry";
	public static final String CLASSIFICATIONTYPE_PARAM_NAME = "ClassificationType";
	public static final String EXPIRATIONDATE_PARAM_NAME = "ExpirationDate";
	public static final String DOCUMENTCLASSNAME_PARAM_NAME ="DocumentClassName";
	public static final String FULLNAME_PARAM_NAME ="FullName";
	public static final String DOCUMENTISSUERCOUNTRYCODE_PARAM_NAME = "documentIssuerCountryCode";
	public static final String HID_IDV_IMAGES_NAMES_LIST_KEY = "HID_IDV_IMAGES_LIST";
}
