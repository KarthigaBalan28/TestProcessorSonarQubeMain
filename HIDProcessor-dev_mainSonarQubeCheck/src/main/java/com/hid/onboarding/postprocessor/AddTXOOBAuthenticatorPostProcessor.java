package com.hid.onboarding.postprocessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.hid.util.HIDFabricConstants;

import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.api.OperationData;
import com.konylabs.middleware.api.ServiceRequest;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

@SuppressWarnings({"java:S1172","java:S112","java:S1125"})
public class AddTXOOBAuthenticatorPostProcessor implements DataPostProcessor2 {
	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.postprocessor.AddTXOOBAuthenticatorPostProcessor.class);
	
	private static final String START_DATE_PARAM = "startDate";
	private static final String EXPIRY_DATE_PARAM = "expiryDate";
	private static final String SEQUENCE_FAILED_PARAM = "sequenceFailed";
	private static final String ERROR_MSG_DETAIL_PARAM = "errorMsgDetail";
	private static final String FRIENDLY_NAME_PARAM = "friendlyName";
	private static final String DEVICE_ID_PARAM ="deviceId";
	private static final String HID_UPDATE_FRIENDLY_NAMES_PARAM = "HIDUpdateFriendlyNames";


	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {

		LOG.debug("HID : In AddTXOOBAuthenticatorPostProcessor");
		boolean sequenceFailed = request.getAttribute(SEQUENCE_FAILED_PARAM) == null ? false
				: request.getAttribute(SEQUENCE_FAILED_PARAM);
		if (sequenceFailed) {
			String errorMsgDetail = request.getAttribute(ERROR_MSG_DETAIL_PARAM) == null ? HIDFabricConstants.AUTH_TX_OOB_FAILURE
					: request.getAttribute(ERROR_MSG_DETAIL_PARAM);
			request.setAttribute(ERROR_MSG_DETAIL_PARAM, errorMsgDetail);
			request.setAttribute(SEQUENCE_FAILED_PARAM, true);
			return result;
		}
		String activeStatusTXOOB = Objects.toString(result.getParamValueByName("active_status"), "");
		String startDate = Objects.toString(result.getParamValueByName(START_DATE_PARAM), "");
		String expDate = Objects.toString(result.getParamValueByName(EXPIRY_DATE_PARAM), "");
		request.setAttribute(START_DATE_PARAM, startDate);
		request.setAttribute(EXPIRY_DATE_PARAM, expDate);
		if ("".equals(activeStatusTXOOB)) {
			LOG.debug("HID : AddTXOOBAuthenticator is failed");
			result.addOpstatusParam(-1);
			request.setAttribute(SEQUENCE_FAILED_PARAM, true);
			request.setAttribute(ERROR_MSG_DETAIL_PARAM, HIDFabricConstants.SERVICE_FAILURE); 
			return result;
		}
		request.setAttribute(SEQUENCE_FAILED_PARAM, false);		
		searchDevices(request, result);
		return result;
	}

	private void searchDevices(DataControllerRequest request, Result result) throws Exception {
		LOG.debug("HID: Inside TXOOB Postprocessor: searchDevices");
		Map<String, Object> inputParams = new HashMap<>();
		String deviceType = AuthenticationConstants.HID_TRANSACTION_DEVICE_TYPE;
		deviceType = GetConfProperties.getProperty(request, deviceType);
		inputParams.put("userId", request.getAttribute("userId"));
		Record newRecord = new Record();
		
		try {
			Result devicesList = invokeService(request, HID_UPDATE_FRIENDLY_NAMES_PARAM, "searchDevices", inputParams);
			Dataset devices = devicesList.getDatasetById("resources");
			List<Record> records = devices.getAllRecords();
			LOG.debug("HID : records: {}", records);
			for (int i = 0; i < records.size(); i++) {
				newRecord = records.get(i);
				String type = newRecord.getParamValueByName("type");
				newRecord.getParamValueByName("type");
				LOG.debug("HID: TX deviceType : {}", deviceType);
				if (deviceType.equals(type)) {
					LOG.debug("inside if deviceType.equals(type) condition");
					request.setAttribute(FRIENDLY_NAME_PARAM, HIDFabricConstants.HID_TX_OOB_FRIENDLY_NAME);
					request.setAttribute(DEVICE_ID_PARAM, newRecord.getParamValueByName("id"));
					updateFriendlyName(request, result);
				}
			}
		} catch (Exception e) {
			LOG.debug("HID : Exception in searchDevices: {}", e.getMessage());
		}

	}

	private void updateFriendlyName(DataControllerRequest request, Result result) throws Exception {		
		Map<String, Object> inputParams = new HashMap<>();		
		inputParams.put(START_DATE_PARAM, request.getAttribute(START_DATE_PARAM));
		inputParams.put(FRIENDLY_NAME_PARAM, request.getAttribute(FRIENDLY_NAME_PARAM));
		inputParams.put(DEVICE_ID_PARAM, request.getAttribute(DEVICE_ID_PARAM));
		inputParams.put(EXPIRY_DATE_PARAM, request.getAttribute(EXPIRY_DATE_PARAM));
		try {
			Result updateResult = invokeService(request, HID_UPDATE_FRIENDLY_NAMES_PARAM, "updateFriendlyNames", inputParams);
			String status = Objects.toString(updateResult.getParamValueByName(FRIENDLY_NAME_PARAM), "");
			if (!("".equals(status))) {				
				LOG.debug("HID : FriendlyName, StartDate and ExpiryDate updated successfully");
			} else {
				LOG.debug("HID : FriendlyName, StartDate and ExpiryDate update failed");
			}
		} catch (Exception e) {
			LOG.debug("HID : Exception in updateFriendlyName: {}", e.getMessage());
		}
	}
	
	private Result invokeService(DataControllerRequest request, String serviceId, String operationId, Map<String, Object> inputParams) throws Exception {
	    try {
	        OperationData serviceData = request.getServicesManager().getOperationDataBuilder()
	            .withServiceId(serviceId)
	            .withOperationId(operationId)
	            .build();
	        ServiceRequest serviceRequest = request.getServicesManager().getRequestBuilder(serviceData)
	            .withInputs(inputParams)
	            .build();
	        return serviceRequest.invokeServiceAndGetResult();
	    } catch (Exception e) {
	        LOG.debug("HID : Exception in service invocation for service: {} and operation: {}: {}", serviceId, operationId, e.getMessage());
	        throw e;
	    }
	}


}
