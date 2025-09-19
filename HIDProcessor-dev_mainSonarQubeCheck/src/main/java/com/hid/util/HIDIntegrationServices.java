package com.hid.util;

import java.util.HashMap;

import com.hid.dataclasses.HIDIntServiceDataclass;
import com.konylabs.middleware.api.OperationData;
import com.konylabs.middleware.api.ServiceRequest;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
@SuppressWarnings({"java:S1118", "java:S1488", "java:S1319", "java:S1192", "java:S3008", "java:S112"})
public class HIDIntegrationServices {
      public static Result call (String serivceName , String operationName, DataControllerRequest request, HashMap<String,Object> headerMap ,HashMap<String,Object> bodyMap ) throws Exception{
    	   OperationData serviceData = request.getServicesManager()
					.getOperationDataBuilder()
					.withServiceId(serivceName)
					.withOperationId(operationName)
					.build();	    
		   ServiceRequest serviceRequest = request.getServicesManager()
					.getRequestBuilder(serviceData)
				    .withInputs(bodyMap)
					.withHeaders(headerMap)
					.build();
		   Result result1 = serviceRequest.invokeServiceAndGetResult();
		   return result1;
      }
      private static  HIDIntServiceDataclass STATIC_PWD = new HIDIntServiceDataclass("STATIC_PWD","HIDPasswordAuthServices","passwordValidation");
      private static  HIDIntServiceDataclass OTP_SMS = new HIDIntServiceDataclass("OTP_SMS","HIDOTPAuthServices","validateOTPAuth");
      private static  HIDIntServiceDataclass OTP_EML = new HIDIntServiceDataclass("OTP_EML","HIDOTPAuthServices","validateOTPAuth");
      private static  HIDIntServiceDataclass SECURE_CODE = new HIDIntServiceDataclass("SECURE_CODE","HIDOTPAuthServices","validateOTPAuth");
      private static  HIDIntServiceDataclass OTP_HWT = new HIDIntServiceDataclass("OTP_HWT","HIDOTPAuthServices","hardwareOTPAuth");
      private static  HIDIntServiceDataclass rmsSessionCreate = new HIDIntServiceDataclass("sessionCreate", "HIDRMSThreatMarkAPI", "sessionCreate");
  	  private static  HIDIntServiceDataclass rmsSessionLogin = new HIDIntServiceDataclass("sessionLogin", "HIDRMSThreatMarkAPI", "sessionLogin");
  	  private static  HIDIntServiceDataclass rmsSessionLogout = new HIDIntServiceDataclass("sessionLogout", "HIDRMSThreatMarkAPI", "sessionLogout");
  	  private static  HIDIntServiceDataclass rmsVisitScore = new HIDIntServiceDataclass("visitScore", "HIDRMSThreatMarkAPI", "visitScore");	
  	  private static  HIDIntServiceDataclass FIDO = new HIDIntServiceDataclass("FIDO","HIDFIDOOrch","authenticate");
  	  public static  HIDIntServiceDataclass getHIDServiceDataObject(String serviceKey) {
    	  switch (serviceKey) {
			case AuthenticationConstants.STATIC_PASSWORD_KEY:
				return STATIC_PWD;
				
			case AuthenticationConstants.SMS_OTP_KEY:
				return OTP_SMS;
				
			case AuthenticationConstants.EMAIL_OTP_KEY:
				return OTP_EML;
				
			case AuthenticationConstants.SECURE_CODE_KEY:
				return SECURE_CODE;
				
			case AuthenticationConstants.HW_OTP_KEY:
				return OTP_HWT;
				
			case AuthenticationConstants.FIDO_KEY:
				return FIDO;
			
			case AuthenticationConstants.HID_RMS_SESSION_CREATE_KEY:
				return rmsSessionCreate ;

			case AuthenticationConstants.HID_RMS_SESSION_LOGIN_KEY:
				return rmsSessionLogin;

			case AuthenticationConstants.HIDRMSsessonLogout:
				return rmsSessionLogout;

			case AuthenticationConstants.HIDRMSvisitScore:
				return rmsVisitScore;
				
			default:
				return rmsSessionCreate;
			}
      }
}
