package com.hid.services;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.registry.AppRegistryException;
import com.hid.dataclasses.HIDIntServiceDataclass;
import com.hid.identity.util.ServiceUtils;
import com.hid.rmsservices.LoginRMSApi;
import com.hid.util.AuthenticationConstants;
import com.hid.util.HIDIntegrationServices;
import com.konylabs.middleware.common.JavaService2;

public class ApproveValidation implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(com.hid.services.ApproveValidation.class);
	private static final String FAILURE = "failure";
	private static final String APPROVE_VALIDATOR_STATUS = "ApproveValidatorStatus";

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		LOG.debug("HID : In ApproveValidation Java Service");
		Result result = new Result();
		String authString = "";
		String password = Objects.toString(((Map<?, ?>) inputArray[1]).get("password"), null);

		try {
			authString = Objects.toString(request.getServicesManager().getResultCache().retrieveFromCache(password),
					null);
		} catch (AppRegistryException | NullPointerException e) {
			LOG.debug("AppRegistryException/ NullPointerException" + e.getMessage());
			e.printStackTrace();
			result.addParam(APPROVE_VALIDATOR_STATUS, FAILURE);
			ServiceUtils.setErrorToResult(result, "AppRegistryException/NullPointerException " + e.getMessage(), -1, 401);
			return result;
		}
		if(authString == null) {
			result.addParam(APPROVE_VALIDATOR_STATUS, FAILURE);
			ServiceUtils.setErrorToResult(result, AuthenticationConstants.APPROVE_STATUS_NOT_KNOWN, -1, 401);
			return result;
		}
		if (authString.isEmpty()) {
			result.addParam(APPROVE_VALIDATOR_STATUS, FAILURE);
			ServiceUtils.setErrorToResult(result, AuthenticationConstants.APPROVE_STATUS_NOT_KNOWN, -1, 401);
			return result;
		}
		result.addOpstatusParam(0);
		result.addHttpStatusCodeParam(200);
		request.setAttribute(APPROVE_VALIDATOR_STATUS, "success");
		ServiceUtils.removeFromCache(request, password);
		return result;
	}
}
