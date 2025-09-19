package com.hid.onboarding.postprocessor;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class UpdateDevicePostProcessor implements DataPostProcessor2 {
	private static final Logger LOG = LogManager.getLogger(com.hid.onboarding.postprocessor.UpdateDevicePostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : In UpdateDevicePostProcessor");
		String detailUpdateDevice = result.getParamValueByName("detail_updateDevice");
		String errorCodeUpdateDevice = result.getParamValueByName("errorCode_updateDevice");
		LOG.debug("HID : UpdateDevice response detail message : {}", detailUpdateDevice);
		LOG.debug("HID : UpdateDevice error code : {}", errorCodeUpdateDevice);
		if(!StringUtils.isEmpty(detailUpdateDevice)) {
			LOG.debug("HID : UpdateDevice detail message is not empty, setting error message");
			request.setAttribute("DeviceId", "");
			request.setAttribute("errMsg", detailUpdateDevice);
		}
		return result;
	}

}
