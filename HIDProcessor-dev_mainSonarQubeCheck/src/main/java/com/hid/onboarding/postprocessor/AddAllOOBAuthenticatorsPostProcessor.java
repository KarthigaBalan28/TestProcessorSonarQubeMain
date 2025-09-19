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

public class AddAllOOBAuthenticatorsPostProcessor implements DataPostProcessor2 {
	private static final Logger LOG = LogManager
			.getLogger(com.hid.onboarding.postprocessor.AddAllOOBAuthenticatorsPostProcessor.class);

	@SuppressWarnings({"java:S3776", "java:S1125", "java:S1192"})
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : In AddAllOOBAuthenticatorsPostProcessor");
		Result res = new Result();

		String cacheAuthKey = Objects.toString(request.getAttribute("cacheAuthKey"), "");
		boolean sequenceFailed = request.getAttribute("sequenceFailed") == null ? false
				: request.getAttribute("sequenceFailed");
		if (cacheAuthKey != null && !cacheAuthKey.isEmpty()) {
			String factor = request.getAttribute("factor") == null ? "" : request.getAttribute("factor");
			LOG.debug("Factor -- > {}", factor);
			if (factor.equals("") || factor.equals("2")) {
				request.getServicesManager().getResultCache().removeFromCache(cacheAuthKey);
				LOG.debug("cacheKey is SuccessFully Removed");
				}
			if (sequenceFailed) {
				LOG.debug("HID : sequenceFailed: {}", sequenceFailed);
				String errorMsgDetail = request.getAttribute("errorMsgDetail") == null ? HIDFabricConstants.SERVICE_FAILURE
						: request.getAttribute("errorMsgDetail");
				String errorDescription = request.getAttribute("error_description") == null ? ""
						: request.getAttribute("error_description");
				String reason = Objects.toString(request.getAttribute("reason"), "");
				if(!reason.isEmpty() && !errorDescription.isEmpty()) {
				res.addStringParam("error_description", errorDescription);
				res.addStringParam("reason", reason);
				}
				res.addStringParam("AddAllOOBServiceError", errorMsgDetail);
				res.addErrMsgParam(errorMsgDetail);		
				res.addStringParam("errorMsg", AuthenticationConstants.INVALID_AUTH_KEY);
				res.addOpstatusParam(-1);
				res.addHttpStatusCodeParam(400);
				return res;
			}
		} else {
			LOG.debug("Cache has been removed");
			res.addOpstatusParam(-1);
			res.addStringParam("AddAllOOBServiceError", AuthenticationConstants.INVALID_AUTH_KEY);
			res.addStringParam("errorMsg", AuthenticationConstants.INVALID_AUTH_KEY);
			res.addErrMsgParam("Invalid request payload");
			res.addHttpStatusCodeParam(400);
			return res;
		}
		return result;
	}

}
