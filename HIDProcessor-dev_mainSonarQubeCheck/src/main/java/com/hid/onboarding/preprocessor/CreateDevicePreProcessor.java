package com.hid.onboarding.preprocessor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.HIDFabricConstants;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class CreateDevicePreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager.getLogger(com.hid.onboarding.preprocessor.CreateDevicePreProcessor.class);

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if (super.execute(inputMap, request, response, result)) {
			LOG.debug("HID : In CreateDevicePreProcessor");
			LocalDateTime now = LocalDateTime.now();
			String start = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(now);
			LocalDateTime next = LocalDateTime.now().plusYears(HIDFabricConstants.YEARS);
			String end = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(next);
			String startDate = (start.indexOf('.') == -1 ? start : start.substring(0, start.indexOf('.')))
					+ HIDFabricConstants.OFFSET_TIME;
			String expDate = (end.indexOf('.') == -1 ? end : end.substring(0, end.indexOf('.')))
					+ HIDFabricConstants.OFFSET_TIME;
			LOG.debug("HID : Setting the value of startDate to : {} and expiryDate to : {} in input parameters", startDate, expDate);
			inputMap.put("startDate", startDate);
			inputMap.put("expiryDate", expDate);
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
