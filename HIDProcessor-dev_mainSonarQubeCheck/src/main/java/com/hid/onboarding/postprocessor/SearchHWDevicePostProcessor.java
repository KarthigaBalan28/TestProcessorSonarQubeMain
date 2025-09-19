package com.hid.onboarding.postprocessor;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class SearchHWDevicePostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.postprocessor.SearchHWDevicePostProcessor.class);

	@SuppressWarnings({"java:S1125","java:S1643"})
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response){
		LOG.debug("HID : In SearchHWDevicePostProcessor");
		try {
			String totalResults = result.getParamValueByName("totalResults");
			request.setAttribute("totalResults", totalResults);

			Dataset resources = result.getDatasetById("resources");
			if (resources != null) {
				boolean value = resources.getAllRecords() == null ? true : resources.getAllRecords().isEmpty();
				String deviceId = "";
				int loopCount = 0;
				if (!value) {
					for (Record r : resources.getAllRecords()) {
						Param device = r.getParam("id");
						if (device != null) {
							deviceId += device.getValue() + "|";
							loopCount++;
							request.setAttribute("sequenceFailed", false);
						}
					}
					request.setAttribute("DeviceId", deviceId);
					request.setAttribute("loop_count", loopCount);
					LOG.debug("HID: SearchHWDevicePostProcessor - DeviceId: {} loop_count {}", deviceId, loopCount);
				}
			}
		} catch (Exception e) {
			LOG.debug("HID : SearchHWDevicePostProcessor : Exception:");
			e.printStackTrace();
		}
		return result;
	}
}
