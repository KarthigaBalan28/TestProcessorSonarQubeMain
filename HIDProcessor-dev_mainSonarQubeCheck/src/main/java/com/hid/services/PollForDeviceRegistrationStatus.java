package com.hid.services;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger; 
import org.apache.logging.log4j.LogManager;

import com.hid.util.GetConfProperties;
import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
@SuppressWarnings({"java:S3776" })
public class PollForDeviceRegistrationStatus implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(com.hid.services.PollForDeviceRegistrationStatus.class);
	private static final String STATUS = "status";
	private static final String HTTP_STATUS_CODE = "httpStatusCode";
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		LOG.debug("HID : In PollForDeviceRegistrationStatus Java service");
		Result result = new Result();
		String deviceId = Objects.toString(((Map<?, ?>) inputArray[1]).get("deviceId"), null);
		String isOnboarding = Objects.toString(((Map<?, ?>) inputArray[1]).get("isOnboarding"), null);
		LOG.debug("HID : Device Id = {}", deviceId);
		if (StringUtils.isNotBlank(deviceId)) {
			int i = 0;
			int maxDuration = 57;
			String registrationJsonString = null;
			while (i < maxDuration) {
				registrationJsonString = Objects
						.toString(request.getServicesManager().getResultCache().retrieveFromCache(deviceId), null);
				TimeUnit.SECONDS.sleep(1);
				if (registrationJsonString != null) {
					break;
				}
				++i;
			}
			LOG.debug("HID : Device registration status JSON String = {}", registrationJsonString);
			result.addStringParam("device_id", deviceId);
			if (registrationJsonString != null) {
				JSONObject authJsonObj = new JSONObject(registrationJsonString);
				String registrationStatus = authJsonObj.optString(STATUS);
				String username = authJsonObj.optString("username");
				LOG.debug("HID : Device Registration Status for username = {} and device id = {} is = {}", username, deviceId, registrationStatus);
				if ("SUCCESS".equalsIgnoreCase(registrationStatus)) {
					LOG.debug("HID : Device registration status is success, returning httpcode 200");
					result.addIntParam(HTTP_STATUS_CODE, 200);
					result.addStringParam(STATUS, registrationStatus);
					result.addStringParam("username", username);
					request.getServicesManager().getResultCache().removeFromCache(deviceId);
					
					if (isOnboarding != null && "true".equals(isOnboarding)) {
						int cacheExpiryValue = 120;
						String cacheExpiryTime = GetConfProperties.getProperty(request, HIDFabricConstants.HID_CACHE_EXPIRY_TIME_IN_SECONDS);		
						if(!cacheExpiryTime.isEmpty()) {
							cacheExpiryValue = Integer.parseInt(cacheExpiryTime);
							LOG.debug("HID : cacheExpiryValue {}", cacheExpiryValue);
						}
						String uuid = UUID.randomUUID().toString();
						result.addParam("auth_key", uuid);
						request.getServicesManager().getResultCache().insertIntoCache(uuid, deviceId, cacheExpiryValue);
						
					}
					
					
				} else {
					LOG.debug("HID : Device registration status is unknown");
					result.addIntParam(HTTP_STATUS_CODE, 401);
					result.addStringParam("errmsg", "HID ActivID Device Registration Status is not known");
					result.addStringParam(STATUS, "UNKNOWN");
					result.addOpstatusParam(-1);
					response.setStatusCode(401);
				}
			} else {
				LOG.debug("HID : Device registration status is unknown");
				result.addIntParam(HTTP_STATUS_CODE, 401);
				result.addStringParam("errmsg", "HID ActivID Device Registration Status is not known");
				result.addStringParam(STATUS, "UNKNOWN");
				result.addOpstatusParam(-1);
				response.setStatusCode(401);
			}
		} else {
			LOG.error("HID : Input device id is null");
			result.addIntParam(HTTP_STATUS_CODE, 400);
			result.addOpstatusParam(-1);
			response.setStatusCode(400);
		}

		return result;

	}
}
