package com.hid.onboarding.postprocessor;

import org.apache.logging.log4j.Logger; 
import org.apache.logging.log4j.LogManager;

import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class CreateDevicePostprocessor implements DataPostProcessor2 {
	private static final Logger LOG = LogManager.getLogger(com.hid.onboarding.postprocessor.CreateDevicePostprocessor.class);
	
	private static final String DEVICE_ID_PARAM ="DeviceId";

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : In CreateDevicePostprocessor");
		String deviceId = (result.getParamValueByName(DEVICE_ID_PARAM) == null) ? "" :result.getParamValueByName(DEVICE_ID_PARAM);
		LOG.debug("HID : Device id is : {}", deviceId);
		request.setAttribute(DEVICE_ID_PARAM, deviceId);
		if(deviceId.isEmpty()) {
			String detailCreateDevice = (result.getParamValueByName("detail_CreateDevice") == null) ? "" :result.getParamValueByName("detail_CreateDevice");
			LOG.debug("HID : Device id is empty, Create Device service message is : {}", detailCreateDevice);
			if(detailCreateDevice.isEmpty()) {
				detailCreateDevice = HIDFabricConstants.SERVICE_FAILURE;
			}
			request.setAttribute("errMsg", detailCreateDevice);
		}
		return result;
	}

}
