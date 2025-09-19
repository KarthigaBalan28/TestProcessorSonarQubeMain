package com.hid.onboarding.postprocessor;

import org.apache.logging.log4j.Logger; 
import org.apache.logging.log4j.LogManager;

import com.hid.util.GetConfProperties;
import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.ehcache.ResultCacheImpl;

import java.util.Objects;
import java.util.UUID;
import java.nio.charset.StandardCharsets;

public class ValidateUserPostProcessor implements DataPostProcessor2 {
	private static final Logger LOG = LogManager.getLogger(com.hid.onboarding.postprocessor.ValidateUserPostProcessor.class);
	
	@SuppressWarnings("java:S1125")
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : In ValidateUserPostProcessor");
		Result res = new Result();
		boolean sequenceFailed = request.getAttribute("sequenceFailed") == null ? false : request.getAttribute("sequenceFailed");
		if(sequenceFailed) {
			String errorMsgDetail = request.getAttribute("errorMsgDetail") == null ? HIDFabricConstants.SERVICE_FAILURE : request.getAttribute("errorMsgDetail");
			res.addStringParam("ActivationCodeError", errorMsgDetail);
			res.addErrMsgParam(errorMsgDetail);
			res.addOpstatusParam(-1);
			res.addHttpStatusCodeParam(400);
			return res;
		}
		int cacheExpiryValue = 120;
		String cacheExpiryTime = GetConfProperties.getProperty(request, HIDFabricConstants.HID_CACHE_EXPIRY_TIME_IN_SECONDS);		
		if(!cacheExpiryTime.isEmpty()) {
			cacheExpiryValue = Integer.parseInt(cacheExpiryTime);
			LOG.debug("HID : cacheExpiryValue {}", cacheExpiryValue);
		}
		String userId = Objects.toString(request.getAttribute("userid"), "");
		String userIdHashCode = Integer.toString(userId.hashCode());
		String uuid = UUID.randomUUID().toString();
		result.addParam("Auth_Key", uuid);
		request.getServicesManager().getResultCache().insertIntoCache(uuid, userIdHashCode, cacheExpiryValue);
		return result;
	}

}
