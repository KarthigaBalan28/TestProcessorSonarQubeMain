package com.hid.onboarding.preprocessor;

import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.AuthenticationConstants;
import com.hid.util.HIDFabricConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class AddPasswordPreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.preprocessor.AddPasswordPreProcessor.class);
	
	private static final String ADD_PASSWORD_SERVICE_ERROR_PARAM = "AddPasswordServiceError";
	private static final String INVALID_REQUEST_PAYLOAD_PARAM = "Invalid request payload";

	@SuppressWarnings({"java:S3776","java:S6541","java:S1854","java:S1185","java:S1481"})
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if (super.execute(inputMap, request, response, result)) {

			String authKey = Objects.toString(inputMap.get("Auth_Key"), "");
			String userId = Objects.toString(inputMap.get("userId"), "");
			String userIdHashCode = Integer.toString(userId.hashCode());
			String factor = Objects.toString(inputMap.get("factor"), "");
			
			if ("".equals(authKey)) {
				result.addOpstatusParam(-1);
				result.addStringParam(ADD_PASSWORD_SERVICE_ERROR_PARAM, AuthenticationConstants.INVALID_AUTH_KEY);
				result.addErrMsgParam(INVALID_REQUEST_PAYLOAD_PARAM);
				return false;
			}

			String cacheAuthKey = Objects
					.toString(request.getServicesManager().getResultCache().retrieveFromCache(authKey));
			if (cacheAuthKey != null && !cacheAuthKey.isEmpty() && cacheAuthKey.equals(userIdHashCode)) {
				
				LOG.debug("HID : cacheAuthKey is present");
				request.setAttribute("cacheAuthKey", authKey);
				LOG.debug("HID : In AddPasswordPreProcessor");
				request.setAttribute("factor", factor);
				
				LocalDateTime now = null;
				long daysToExpire = 0;
				
		      String startDateFromReq = Objects.toString(inputMap.get("startDate"), "");
		      
		      if (!startDateFromReq.isEmpty() && StringUtils.isNumeric(startDateFromReq)) {
		        long epochTime = Long.parseLong(startDateFromReq);
		        now = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochTime), ZoneId.systemDefault());
			      } else {
			        now = LocalDateTime.now();
			      } 
			      String start = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(now);
			      String expiryTimeDuration = GetConfProperties.getProperty(request, HIDFabricConstants.PWD_EXPIRY_TIME);
			      if (!expiryTimeDuration.isEmpty() && StringUtils.isNumeric(expiryTimeDuration)) {
						daysToExpire = Long.parseLong(expiryTimeDuration);
				  } 
			      LocalDateTime next = LocalDateTime.now().
			    		  plusDays(HIDFabricConstants.DEFAULT_PWD_EXPIRY_DAYS);
			      
			      String end = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(next);
			      String offSetTime = GetConfProperties.getProperty(request, "HID_OFFSET_TIME");
			      String startDate = "";
			      String expDate = "";
			      if (!offSetTime.isEmpty()) {
			        startDate = ((start.indexOf('.') == -1) ? start : start.substring(0, start.indexOf('.'))) + offSetTime;
			        expDate = ((end.indexOf('.') == -1) ? end : end.substring(0, end.indexOf('.'))) + offSetTime;
			      } else {
			        startDate = ((start.indexOf('.') == -1) ? start : start.substring(0, start.indexOf('.'))) 
			        		+ HIDFabricConstants.OFFSET_TIME;
			        expDate = ((end.indexOf('.') == -1) ? end : end.substring(0, end.indexOf('.'))) 
			        		+ HIDFabricConstants.OFFSET_TIME;
			      } 
	
				String authType = GetConfProperties.getProperty(request, AuthenticationConstants.HID_PASSWORD_AUTHTYPE);
				LOG.debug("HID : Value of HID_PASSWORD_AUTHTYPE from server settings is : {}", authType);
				if (!authType.isEmpty()) {
					LOG.debug("HID : Setting the value of AuthenticatorType in input parameter");
					inputMap.put("authType", authType);
				}
				LOG.debug("HID : Setting the value of startDate to : {} and expDate to : {} in input parameters", startDate, expDate);
				inputMap.put("startDate", startDate);
				inputMap.put("expDate", expDate);
				return true;
			} else {
				result.addOpstatusParam(-1);
				result.addStringParam(ADD_PASSWORD_SERVICE_ERROR_PARAM, AuthenticationConstants.INVALID_AUTH_KEY);
				result.addErrMsgParam(INVALID_REQUEST_PAYLOAD_PARAM);
				return false;
			}
		} else {
			result.addOpstatusParam(-1);
			result.addStringParam(ADD_PASSWORD_SERVICE_ERROR_PARAM, AuthenticationConstants.INVALID_AUTH_KEY);
			result.addErrMsgParam(INVALID_REQUEST_PAYLOAD_PARAM);
			return false;
		}
	}

}
