package com.hid.authentication.preprocessor;

import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.api.OperationData;
import com.konylabs.middleware.api.ServiceRequest;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class InitiateSecondFactorPreprocessor implements DataPreProcessor2 {
	
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,  // NOSONAR
			Result result) {
		try {
		 OperationData serviceData = request.getServicesManager()
					.getOperationDataBuilder()
					.withServiceId("OTPTemp")
					.withOperationId("send")
					.build();
		 HashMap<String,Object> bodyMap = new HashMap<>();
		 HashMap<String,Object> headerMap = new HashMap<>();
		 String username = Objects.toString(inputMap.get("username").toString(),"");
		 String password = Objects.toString(inputMap.get("password").toString(),"");
		 if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			 result.addOpstatusParam(-2);
			 result.addErrMsgParam("Username or password is Blank");
			 return false;
		 }
		 String authType =  GetConfProperties.getProperty(request, AuthenticationConstants.HID_OTP_EML_ENV_VARIABLE_KEY);
		 bodyMap.put("AuthenticationType", authType);
		 bodyMap.put("username", username);
		 bodyMap.put("password", password);
		 String accessToken = request.getAttribute("access_token");
		 headerMap.put("Authorization",accessToken);
		 ServiceRequest serviceRequest = request.getServicesManager().getRequestBuilder(serviceData)
					.withInputs(bodyMap)
					.withHeaders(headerMap)
					.build();
		 Result result1 = serviceRequest.invokeServiceAndGetResult();
		 String sentStatus = result1.getParamValueByName("OOB_SENT");
		 if(!StringUtils.isEmpty(sentStatus)) {
			 result.addOpstatusParam(0);
			 result.addStringParam("OOB_SENT", sentStatus);
		 }else {
			 result.addOpstatusParam(-2);
			 String errMsg = result1.getParamValueByName("errmsg");
			 result.addErrMsgParam(errMsg);
		 }
		}catch(Exception e) {
			result.addOpstatusParam(-1);
			result.addErrMsgParam(e.getMessage());
		}
		 return false;
	}

}
