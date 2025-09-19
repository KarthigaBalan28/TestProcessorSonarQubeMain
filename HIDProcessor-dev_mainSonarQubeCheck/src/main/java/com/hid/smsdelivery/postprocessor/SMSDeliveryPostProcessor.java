package com.hid.smsdelivery.postprocessor;

import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.util.Objects;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.hid.smsdelivery.constants.SMSConstants;
import com.hid.smsdelivery.util.SendSMSOTPUtil;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.registry.AppRegistryException;
import com.hid.dataclasses.HIDIntServiceDataclass;

public class SMSDeliveryPostProcessor implements DataPostProcessor2 {
	private static final Logger LOG = LogManager
			.getLogger(com.hid.smsdelivery.postprocessor.SMSDeliveryPostProcessor.class);

	@Override
	@SuppressWarnings({"java:S3776","java:S6541","java:S1854","java:S1185", "java:S1192", "java:S1319"}) 
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : Inside SMSDeliveryPostProcessor");
		String errorMsg = Objects.toString(result.getParamValueByName("errorMsg"));
		String searchUserErrorMsg = result.getParamValueByName("searchUserErrorMsg");
		String reason = result.getParamValueByName("reason");
		String otpServiceFailed = result.getParamValueByName("OTPServiceFailed");
		String searchUserFailed = result.getParamValueByName("searchUserFailed");
		Result result1 =  new Result();
		if("true".equalsIgnoreCase(searchUserFailed)) {
			LOG.debug("HID : Inside SMSDeliveryPostProcessor searchUserFailed");
			resetResult(result1);
			result1.addStringParam("reason", reason);
			result1.addStringParam("errorMsg", searchUserErrorMsg);
			return result1;			
		} 
		if("true".equalsIgnoreCase(otpServiceFailed)) {
			LOG.debug("HID : Inside SMSDeliveryPostProcessor OTPServiceFailed");
			Result result2 =  new Result();
			resetResult(result2);
			result2.addStringParam("reason", reason);
			result2.addStringParam("errorMsg", errorMsg);
			return result2;
		}
		if (!validateAndPopulateReq(request, result)) {
			LOG.debug("HID : Inside SMSDeliveryPostProcessor: OTP/PhonNo is missing");
			Result result3 =  new Result();
			resetResult(result3);
			result3.addErrMsgParam(SMSConstants.GENERIC_ERROR);
			result3.addStringParam("errorMsg", SMSConstants.GENERIC_ERROR);
			return result3;
		}		
		Result smsResult = smsServiceCall(request, result);
		return mixResult(result, smsResult);
	}

	private boolean validateAndPopulateReq(DataControllerRequest request, Result result) {
		String phoneNo = Objects.toString(result.getParamValueByName(SMSConstants.PHONE_NUM_PARAM), "");
		String otp = Objects.toString(result.getParamValueByName(SMSConstants.OTP_PARAM), "");
		if (StringUtils.isEmpty(phoneNo) || StringUtils.isEmpty(otp)) {
			return false;
		}
		request.setAttribute(SMSConstants.PHONE_NUM_PARAM, formatPhoneNo(phoneNo));
		request.setAttribute(SMSConstants.OTP_PARAM, otp);
		return true;
	}

	private String formatPhoneNo(String phoneNo) {
		int start = 0;
		for (Character c : phoneNo.toCharArray()) {
			if (!Character.isDigit(c) || c == '0') {
				start++;
			} else {
				break;
			}
		}
		return phoneNo.substring(start);
	}

	private Result mixResult(Result result, Result smsResult) {
		resetResult(result);
		String opstatus = smsResult.getOpstatusParamValue();
		LOG.debug("HID : Inside SMSDeliveryPostProcessor mixResult opstatus:{} " , opstatus);
		LOG.debug("HID : Inside SMSDeliveryPostProcessor smsResult :{} " , smsResult.getAllParams());
		if (StringUtils.isEmpty(opstatus) || !StringUtils.isNumeric(opstatus) || Integer.parseInt(opstatus) != 0) {
			result.addOpstatusParam(-1);
			result.addErrMsgParam("SENDKMSSMS Service Failed");
			return result;
		}
		result.addStringParam(SMSConstants.OTP_SUCCESS_PARAM, "true");
		return result;
	}

	private Result smsServiceCall(DataControllerRequest request, Result result) {
		String sendSMS = SMSConstants.SEND_KMS_SMS;
		String serviceName = "";
		String operationName = "";
		Result result1 = new Result();
		HIDIntServiceDataclass serviceData = SendSMSOTPUtil.getHIDServiceDataObject(sendSMS);
		if (serviceData != null) {
			serviceName = serviceData.getServiceName();
			operationName = serviceData.getOperationName();
			LOG.debug("HID : SMSDeliveryPostProcessor --->smsServiceCall: serviceName:{} operationName:{}" , serviceName, operationName);
			try {
				result1 = SendSMSOTPUtil.call(serviceName, operationName, request, formHeaderMap(),
						formRequestMap(request, result));

			} catch (Exception e) {
				String errorMsg = "Exception while invoking the service " + serviceName + "." + operationName
						+ " with message " + e.getMessage();
				result1.addStringParam("errorMsg", errorMsg);
				result1.addOpstatusParam(-1);
				result1.addHttpStatusCodeParam(401);
				LOG.error(errorMsg);
				e.printStackTrace();
				return result1;
			}
		} else {
			String errorMsg = "Exception while invoking the service " + serviceName + "." + operationName;
			result1.addStringParam("errorMsg", errorMsg);
			result1.addOpstatusParam(-1);
			result1.addHttpStatusCodeParam(401);
			return result1;
		}
		return result1;
	}

	private void resetResult(Result result) {
		result.addStringParam(SMSConstants.PHONE_NUM_PARAM, "");
		result.addStringParam(SMSConstants.OTP_SUCCESS_PARAM, "");
		result.addOpstatusParam(-1);
		result.addHttpStatusCodeParam(400);
	}

	public HashMap<String, Object> formRequestMap(DataControllerRequest request, Result result) {
		String phoneNo = Objects.toString(result.getParamValueByName(SMSConstants.PHONE_NUM_PARAM), "");
		HashMap<String, Object> bodyMap = new HashMap<String, Object>();
		bodyMap.put("sendToMobiles", phoneNo);
		bodyMap.put("smsText", formMessage(request));
		return bodyMap;
	}

	public HashMap<String, Object> formHeaderMap() {
		return new HashMap<String, Object>();
	}

	private String formMessage(DataControllerRequest request) {
		String otp = request.getAttribute("otp");
		String defaultStr = "Dear customer otp is #";
		String custTemp;
		try {
			custTemp = GetConfProperties.getProperty(request, "HID_SMS_MSG_TEMPLATE");
		} catch (Exception e) {
			custTemp = "";
		}
		if (!StringUtils.isEmpty(custTemp) && custTemp.indexOf("#") != -1) {
			defaultStr = custTemp;
		}
		return defaultStr.replace("#", otp);
	}
}
