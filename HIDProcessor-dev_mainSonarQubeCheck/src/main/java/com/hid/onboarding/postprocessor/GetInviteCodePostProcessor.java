package com.hid.onboarding.postprocessor;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.hid.util.AuthenticationConstants;
import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetInviteCodePostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.postprocessor.GetInviteCodePostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : In GetInviteCodePostProcessor");
		Result res = new Result();
		String isOnboardingFlow = Objects.toString(request.getAttribute("isOnboardingFlow"), "");
		String cacheAuthKey = Objects.toString(request.getAttribute("cacheAuthKey"), "");
		if("".equals(isOnboardingFlow) || isOnboardingFlow.equals("false")) {
			res = getDeviceProvisionDetails(result, request);			
		}
		else if (cacheAuthKey != null && !cacheAuthKey.isEmpty()) {
			request.getServicesManager().getResultCache().removeFromCache(cacheAuthKey);
			LOG.debug("cacheKey is SuccessFully Removed");
			res = getDeviceProvisionDetails(result, request);			
		} else {
			LOG.debug("Cache has been removed");
			res.addOpstatusParam(-1);
			res.addStringParam("GetInviteCodeServiceError", AuthenticationConstants.INVALID_AUTH_KEY);
			res.addErrMsgParam("Invalid request payload");
			res.addHttpStatusCodeParam(400);
			return res;
		}
		return res;
	}

	@SuppressWarnings({"java:S3457","java:S2629"})
	private Result getDeviceProvisionDetails(Result result, DataControllerRequest request) {
		String deviceId = request.getAttribute("DeviceId");
		String provisionMsg = result.getParamValueByName("provisionMsg");
		LOG.debug(String.format("HID : Device id is {} and provisioning message is {}", deviceId,
				provisionMsg));
		if (StringUtils.isEmpty(deviceId)) {
			result.addOpstatusParam(-1);
			String errMsg = request.getAttribute("errMsg");
			errMsg = StringUtils.isEmpty(errMsg) ? HIDFabricConstants.SERVICE_FAILURE : errMsg;
			LOG.debug("HID : Device id is empty, error message is : {}", errMsg);
			result.addErrMsgParam(errMsg);
		} else if (StringUtils.isEmpty(provisionMsg)) {
			result.addOpstatusParam(-1);
			String errMsg = result.getParamValueByName("detail");
			errMsg = StringUtils.isEmpty(errMsg) ? HIDFabricConstants.SERVICE_FAILURE : errMsg;
			LOG.debug("HID : Provisioning message is empty, error message is : {}", errMsg);
			result.addErrMsgParam(errMsg);
		}

		return result;
		
	}

}
