package com.hid.onboarding.preprocessor;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.HIDFabricConstants;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class DeviceProvisionPreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.preprocessor.DeviceProvisionPreProcessor.class);

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if (super.execute(inputMap, request, response, result)) {
			LOG.debug("HID : In DeviceProvisionPreProcessor");
			String deviceId = request.getAttribute("DeviceId");
			LOG.debug("HID : Device id is : {}", deviceId);
			if (StringUtils.isEmpty(deviceId)) {
				result.addOpstatusParam(-1);
				String errMsg = request.getAttribute("errMsg");
				errMsg = StringUtils.isEmpty(errMsg) ? HIDFabricConstants.SERVICE_FAILURE : errMsg;
				LOG.debug("HID : Device id is null, error message is : {}", errMsg);
				result.addErrMsgParam(errMsg);
				return false;
			}
			inputMap.put("DeviceID", deviceId);
			String deviceType = GetConfProperties.getProperty(request, AuthenticationConstants.HID_DEVICE_TYPE);
			LOG.debug("HID : the value of DEVICE_TYPE from server settings is : {}", deviceType);
			if (!StringUtils.isEmpty(deviceType)) {
				LOG.debug("HID : setting the value of deviceType to : {}", deviceType);
				inputMap.put("deviceType", deviceType);
			}
			return true;
		}
		return false;
	}

}
