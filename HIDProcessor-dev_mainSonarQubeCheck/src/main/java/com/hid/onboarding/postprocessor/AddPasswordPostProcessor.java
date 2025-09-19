package com.hid.onboarding.postprocessor;

import org.apache.logging.log4j.Logger;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;

import com.hid.util.AuthenticationConstants;
import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class AddPasswordPostProcessor implements DataPostProcessor2 {
	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.postprocessor.AddPasswordPostProcessor.class);
	
	private static final String ERROR_CODE_PARAM = "errorCode";

	@SuppressWarnings({"java:S3457", "java:S2629", "java:S1066"})
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : In AddPasswordPostProcessor");
		String cacheAuthKey = Objects.toString(request.getAttribute("cacheAuthKey"), "");
		String factor = request.getAttribute("factor") == null ? "" : request.getAttribute("factor");
		String scimTypePwd = (result.getParamValueByName("scimType_Pwd") == null) ? ""
				: result.getParamValueByName("scimType_Pwd");
		String errorCode = (result.getParamValueByName(ERROR_CODE_PARAM) == null) ? ""
				: result.getParamValueByName(ERROR_CODE_PARAM);
		LOG.debug(String.format("HID: Values of scimType_Pwd : {} and errorcode : {}", scimTypePwd, errorCode));
		if (scimTypePwd.equals("uniqueness") || errorCode.equals("1103")) {
			request.setAttribute("isPasswordAdded", false);
			LOG.debug("HID : Static password authenticator already exists");
			result.addStringParam("AddPasswordServiceError", HIDFabricConstants.SERVICE_FAILURE);
			result.addErrMsgParam("Invalid request");
		}
		result.addParam(ERROR_CODE_PARAM, errorCode);
		if (cacheAuthKey != null && !cacheAuthKey.isEmpty()) {
			if(errorCode.equals("") && factor.equals("2")) {
				request.getServicesManager().getResultCache().removeFromCache(cacheAuthKey);
				LOG.debug("cacheKey is SuccessFully Removed");
			}
		}
			
		return result;
	}

}
