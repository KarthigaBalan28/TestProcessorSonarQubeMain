package com.hid.customotp;

import java.util.HashMap;
import com.hid.customotp.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class CustomOTPOrchPostProcessor implements DataPostProcessor2{
    private static final String GENERIC_ERROR = "Failed to send OTP! please try again";
    
    private static final Logger LOG = LogManager
			.getLogger(com.hid.customotp.CustomOTPOrchPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : Inside CustomOTPOrchPostProcessor");
		String opstatus = result.getOpstatusParamValue();
		if(StringUtils.isEmpty(opstatus) || !StringUtils.isNumeric(opstatus) || Integer.parseInt(opstatus) != 0) {
			resetResult(result);
			return result;
		}
		if(!validateAndPopulateReq(request, result)) {
			resetResult(result);
			result.addOpstatusParam(-2);
			result.addErrMsgParam(CustomOTPConstants.GENERIC_ERROR);
			return result;
		}
		Result smsResult = smsServiceCall(request);
		return mixResult(result, smsResult);
	}

	private boolean validateAndPopulateReq(DataControllerRequest request, Result result) {
		String phoneNo = result.getParamValueByName(CustomOTPConstants.PHONE_NUM_PARAM);
		String otp = result.getParamValueByName(CustomOTPConstants.OTP_PARAM);
		if(StringUtils.isEmpty(phoneNo) || StringUtils.isEmpty(otp)) return false;
		request.setAttribute(CustomOTPConstants.PHONE_NUM_PARAM, formatPhoneNo(phoneNo));
		request.setAttribute(CustomOTPConstants.OTP_PARAM, otp);
		return true;
	}

	private String formatPhoneNo(String phoneNo) {
	    int start = 0;
	    for(Character c: phoneNo.toCharArray()){
	    	if(!Character.isDigit(c) || c == '0') {
	    		start++;
	    	}else {
	    		break;
	    	}
	    }
	    return phoneNo.substring(start);
	}

	private Result mixResult(Result result, Result smsResult) {
		resetResult(result);
		String opstatus = smsResult.getOpstatusParamValue();
		if(StringUtils.isEmpty(opstatus) || !StringUtils.isNumeric(opstatus) || Integer.parseInt(opstatus) != 0) {
			result.addOpstatusParam(-1);
			result.addErrMsgParam("Service Failed");
			return result;
		}
		result.addStringParam(CustomOTPConstants.OTP_SUCCESS_PARAM, "true");
		return result;
	}

	private Result smsServiceCall(DataControllerRequest request) {
		SMSCarrierService service = DependencyManager.getCarrierService();
		return service.sendSMS(request);
	}
	
	private void resetResult(Result result) {
		result.addStringParam(CustomOTPConstants.PHONE_NUM_PARAM, "");
		result.addStringParam(CustomOTPConstants.OTP_PARAM, "");
		result.addStringParam(CustomOTPConstants.OTP_SUCCESS_PARAM, "false");
	}



	
}
