package com.hid.authentication.postprocessor;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hid.util.AuthenticationConstants;
import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetDevicesOrchPostProcessor implements DataPostProcessor2 {
	private static final Logger LOG = LogManager
			.getLogger(com.hid.authentication.postprocessor.GetDevicesOrchPostProcessor.class);

	
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		Result res= new Result();
		boolean sequenceFailed = request.getAttribute("sequenceFailed") == null ? false //NOSONAR
				: request.getAttribute("sequenceFailed"); 
		if (sequenceFailed) {
			String errorMsgDetail = request.getAttribute("errorMsgDetail") == null ? HIDFabricConstants.SERVICE_FAILURE
					: request.getAttribute("errorMsgDetail");
			res.addErrMsgParam(errorMsgDetail);
			res.addStringParam("searchDevicesServiceError", AuthenticationConstants.INVALID_AUTH_KEY);
			res.addOpstatusParam(-1);
			res.addHttpStatusCodeParam(400);
			return res;

		}
		String isLoginFlow = Objects.toString(request.getAttribute("isLoginFlow"), "");
		String transactionId = Objects.toString(request.getAttribute("transactionId"), "");
		if("".equals(isLoginFlow) || isLoginFlow.equals("false")) {
			return result;
		}		
		if (transactionId != null && !transactionId.isEmpty()) {
			request.getServicesManager().getResultCache().removeFromCache(transactionId);
			LOG.debug("transactionId is successFully removed from cache");
		} else {
			LOG.debug("Cache has been removed");
			res.addOpstatusParam(-1);
			res.addStringParam("searchDevicesServiceError", AuthenticationConstants.INVALID_AUTH_KEY);
			res.addErrMsgParam("Invalid request payload");
			res.addHttpStatusCodeParam(400);
			return res;
		}
		
		return result;
	}

}
