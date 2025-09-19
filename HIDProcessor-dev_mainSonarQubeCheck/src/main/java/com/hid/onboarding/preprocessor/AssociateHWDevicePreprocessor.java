package com.hid.onboarding.preprocessor;

import java.util.HashMap;

import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.HIDFabricConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class AssociateHWDevicePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.preprocessor.AssociateHWDevicePreprocessor.class);
	
	private static final String LOOP_COUNT_PARAM = "loop_count";
	private static final String DEVICE_ID_PARAM ="DeviceId";

	@SuppressWarnings({"java:S1125","java:S3457","java:S5411","java:S2629"})
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) {
			try {
				LOG.debug("HID:In AssociateHWDevicePreprocessor");

				Boolean totalResults = (request.getAttribute("totalResults") == null) ? true
						: !request.getAttribute("totalResults").equals("0");

				LOG.debug("HID:AssociateHWDevicePreprocessor : totalResults: {}", totalResults);
				
				if (!totalResults) {
					LOG.debug("HID : Devices not found");
					result.addOpstatusParam(-1);
					result.addErrMsgParam(HIDFabricConstants.DEVICES_NOT_FOUND);
					request.setAttribute("sequenceFailed", true);
					request.setAttribute("errorMsgDetail", HIDFabricConstants.DEVICES_NOT_FOUND);
					return false;
				}
				LOG.debug("HID : AssociateHWDevicePreprocessor : DeviceId: {}", request.getAttribute(DEVICE_ID_PARAM));
				
				
				if(request.getAttribute(DEVICE_ID_PARAM)!=null) {
					String deviceId =  request.getAttribute(DEVICE_ID_PARAM);
					deviceId = deviceId.substring(0,deviceId.length()-1);
					request.addRequestParam_(DEVICE_ID_PARAM, deviceId);
					LOG.debug("HID : AssociateHWDevicePreprocessor : DeviceId: {}", inputMap.get(DEVICE_ID_PARAM));
				}
				
				if(request.getAttribute(LOOP_COUNT_PARAM) != null) {
					inputMap.put(LOOP_COUNT_PARAM, request.getAttribute(LOOP_COUNT_PARAM)+"");
				}
				
				LOG.debug("HID: SearchHWDevicePostProcessor InputMap: Keys: {}", inputMap.keySet().toString());
				LOG.debug("HID: SearchHWDevicePostProcessor InputMap:Values: {}", inputMap.values().toString());
				request.setAttribute("sequenceFailed", false);
			} catch (Exception e) {
				LOG.debug("HID : AssociateHWDevicePreprocessor : Exception:");
				e.printStackTrace();
			}
			return true;
	}

}
