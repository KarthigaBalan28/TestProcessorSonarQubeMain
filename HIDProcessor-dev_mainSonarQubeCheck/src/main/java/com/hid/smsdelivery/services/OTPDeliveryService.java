package com.hid.smsdelivery.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hid.dataclasses.HIDIntServiceDataclass;
import com.hid.smsdelivery.constants.SMSConstants;
import com.hid.smsdelivery.util.SendSMSOTPUtil;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.registry.AppRegistryException;

@SuppressWarnings({"java:S1450","java:S3776", "java:S1192", "java:S1854", "java:S1488", "java:S1172"})
public class OTPDeliveryService implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(com.hid.smsdelivery.services.OTPDeliveryService.class);
	private String serviceName = "";
	private String operationName = "";
	private String authKey = "";
	private String msgId = "";
	private String isAppliance = "false";

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		LOG.debug("HID : Inside OTPDeliveryService");
		Result result = new Result();
		authKey = Objects.toString(((Map<?, ?>) inputArray[1]).get("Auth_Key"), "");
		msgId = Objects.toString(((Map<?, ?>) inputArray[1]).get("msgId"), "");
		request.setAttribute("msgId", msgId);
		String isHIDGatewayEnabled = GetConfProperties.getProperty(request, SMSConstants.HID_IS_GATEWAY_ENABLED);
		isAppliance = GetConfProperties.getProperty(request, SMSConstants.HID_APPLIANCE_ENV_KEY);
		LOG.debug("HID : OTPDeliveryService ---> isHIDGatewayEnabled: {} ", isHIDGatewayEnabled);
		if (isHIDGatewayEnabled != null && !isHIDGatewayEnabled.trim().isEmpty()) {
			if (isHIDGatewayEnabled.equalsIgnoreCase("true")) {
				if (authKey != null && !authKey.isEmpty()) {
					request.setAttribute("Auth_Key", authKey);
					result = handleOnboardingOTPService(inputArray, request);
				} else if (msgId != null && !msgId.isEmpty()) {
					request.setAttribute("msgId", msgId);
					result = handleLoginOTPService(inputArray, request);
				} else {
					result = handleGenericOTPService(inputArray, request);
				}
			} else {
				if (authKey != null && !authKey.isEmpty()) {
					result = handleOnboardingOTPServiceKMS(inputArray, request);
				} else if (msgId != null && !msgId.isEmpty()) {
					result = handleLoginOTPServiceKMS(inputArray, request);
				} else {
					result = handleKMSOTPService(inputArray, request);
				}
			}
		} else {
			String errorMsg = SMSConstants.PROPERTY_NOT_FOUND;
			result = setErrorMsg(errorMsg);
		}
		return result;
	}

	private Result handleKMSOTPService(Object[] inputArray, DataControllerRequest request) {
		LOG.debug("HID : Inside handleKMSOTPService method");
		Result res = new Result();
		String sendOTPService = "";
		if (isAppliance != null && !isAppliance.trim().isEmpty() && isAppliance.equals("true")) {
			sendOTPService = SMSConstants.HID_SEND_OTP_APP_KMS;
		} else {
			sendOTPService = SMSConstants.HID_SEND_OTP_KMS;
		}
		res = invokeService(sendOTPService, inputArray, request);
		return res;
	}

	private Result handleLoginOTPService(Object[] inputArray, DataControllerRequest request) {
		LOG.debug("HID : Inside handleLoginOTPService method");
		Result res = new Result();
		String sendOTPLogin = SMSConstants.HID_SEND_OTP_LOGIN;
		res = invokeService(sendOTPLogin, inputArray, request);
		return res;
	}

	private Result handleGenericOTPService(Object[] inputArray, DataControllerRequest request) {
		LOG.debug("HID : Inside handleGenericOTPService method");
		Result res = new Result();
		String sendOTPLogin = SMSConstants.HID_SEND_OTP_GENERIC;
		res = invokeService(sendOTPLogin, inputArray, request);
		return res;
	}

	private Result handleOnboardingOTPService(Object[] inputArray, DataControllerRequest request) {
		LOG.debug("HID : Inside handleOnboardingOTPService method");
		Result res = new Result();
		String sendOTP = SMSConstants.HID_SEND_OTP_ONBORDING;
		res = invokeService(sendOTP, inputArray, request);
		return res;
	}

	private Result handleOnboardingOTPServiceKMS(Object[] inputArray, DataControllerRequest request) {
		LOG.debug("HID : Inside handleOnboardingOTPServiceKMS method");
		Result res = new Result();
		String sendOTP = "";
		if (isAppliance != null && !isAppliance.trim().isEmpty() && isAppliance.equals("true")) {
			sendOTP = SMSConstants.HID_SEND_OTP_ONBORDING_APP_KMS;
		} else {
			sendOTP = SMSConstants.HID_SEND_OTP_ONBORDING_KMS;
		}
		
		res = invokeService(sendOTP, inputArray, request);
		return res;
	}

	private Result handleLoginOTPServiceKMS(Object[] inputArray, DataControllerRequest request) {
		LOG.debug("HID : Inside handleLoginOTPServiceKMS method");
		Result res = new Result();
		String sendOTP = "";
		if (isAppliance != null && !isAppliance.trim().isEmpty() && isAppliance.equals("true")) {
			sendOTP = SMSConstants.HID_SEND_OTP_LOGIN_APP_KMS;
		} else {
			sendOTP = SMSConstants.HID_SEND_OTP_LOGIN_KMS;
		}
		res = invokeService(sendOTP, inputArray, request);
		return res;
	}

	private HashMap<String, Object> getBodyMap(Object[] inputArray, DataControllerRequest request) {
		HashMap<String, Object> bodyMap = new HashMap<String, Object>();
		bodyMap.put("username", Objects.toString(((Map<?, ?>) inputArray[1]).get("username"), null));
		bodyMap.put("AuthenticationType",
				Objects.toString(((Map<?, ?>) inputArray[1]).get("AuthenticationType"), null));
		bodyMap.put("password", Objects.toString(((Map<?, ?>) inputArray[1]).get("password"), null));
		bodyMap.put("userId", Objects.toString(((Map<?, ?>) inputArray[1]).get("userId"), null));
		bodyMap.put("Auth_Key", Objects.toString(((Map<?, ?>) inputArray[1]).get("Auth_Key"), null));
		bodyMap.put("correlationId", Objects.toString(((Map<?, ?>) inputArray[1]).get("correlationId"), null));
		bodyMap.put("isPasswordRequired",
				Objects.toString(((Map<?, ?>) inputArray[1]).get("isPasswordRequired"), null));
		bodyMap.put("msgId", Objects.toString(((Map<?, ?>) inputArray[1]).get("msgId"), null));
		bodyMap.put("factor", Objects.toString(((Map<?, ?>) inputArray[1]).get("factor"), null));
		bodyMap.put("AuthenticatorType", Objects.toString(((Map<?, ?>) inputArray[1]).get("AuthenticatorType"), null));
		bodyMap.put("AuthenticatorValue",
				Objects.toString(((Map<?, ?>) inputArray[1]).get("AuthenticatorValue"), null));
		bodyMap.put("OOB_PIN",
				Objects.toString(((Map<?, ?>) inputArray[1]).get("OOB_PIN"), null));
		
		return bodyMap;
	}

	private HashMap<String, Object> getHeadersMap(Object[] inputArray, DataControllerRequest request) {
		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		return headerMap;
	}

	private Result setErrorMsg(String errorMsg) {
		Result res = new Result();
		res.addStringParam("errorMsg", errorMsg);
		res.addOpstatusParam(-1);
		res.addHttpStatusCodeParam(401);
		LOG.error("HID : setErrorMsg --> errorMsg is: {} ", errorMsg);
		return res;
	}

	private Result invokeService(String serviceKey, Object[] inputArray, DataControllerRequest request) {
		Result res = new Result();
		HIDIntServiceDataclass serviceData = SendSMSOTPUtil.getHIDServiceDataObject(serviceKey);
		if (serviceData != null) {
			serviceName = serviceData.getServiceName();
			operationName = serviceData.getOperationName();
			LOG.debug("HID : OTPDeliveryService ---> serviceName:{} and operationName:{}" , serviceName , operationName);
			try {
				res = SendSMSOTPUtil.call(serviceName, operationName, request, getHeadersMap(inputArray, request),
						getBodyMap(inputArray, request));

			} catch (Exception e) {
				String errorMsg = "Exception while invoking the service " + serviceName + "." + operationName
						+ " with message " + e.getMessage();
				LOG.error("HID: errorMsg:{} " , errorMsg);
				res = setErrorMsg(errorMsg);
				e.printStackTrace();
				return res;
			}
		} else {
			String errorMsg = String.format("%s Service is not available in Fabric", serviceKey);
			res = setErrorMsg(errorMsg);
		}
		return res;
	}

}
