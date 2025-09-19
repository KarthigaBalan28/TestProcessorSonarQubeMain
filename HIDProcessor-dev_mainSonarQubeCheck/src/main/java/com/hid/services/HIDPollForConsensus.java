package com.hid.services;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
@SuppressWarnings({"java:S2629"})
public class HIDPollForConsensus implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(com.hid.services.HIDPollForConsensus.class);
	private static final String HTTP_STATUS_CODE = "httpStatusCode";
	private static final String AUTH_STATUS = "auth_status";
	private static final String ERR_MSG = "errmsg";

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		
		LOG.debug("HID : In HIDPollForConsensus Java Service");
		Result result = new Result();
		String authReqId = Objects.toString(((Map<?, ?>) inputArray[1]).get("mfa_key"), null);
		LOG.debug("HID : Auth Req Id = {}", authReqId);
		if (StringUtils.isNotBlank(authReqId)) {
			int i =0;
			int maxDuration = 57;
			String authJsonString = null;
			while (i<maxDuration) {
				authJsonString = Objects.toString(request.getServicesManager().getResultCache().retrieveFromCache(authReqId), null);
				TimeUnit.SECONDS.sleep(1);
				if (authJsonString!=null) {
					break;
				}
				++i;
			}
			LOG.debug("Auth JSON String = {}",  String.valueOf(authJsonString));
			result.addStringParam("auth_req_id", authReqId);
			if (authJsonString != null) {
				JSONObject authJsonObj = new JSONObject(authJsonString);
				LOG.debug("HID : polling json response : {}", authJsonObj);
				String authStatus = authJsonObj.optString("clientapprovalstatus");
				String accessToken = authJsonObj.optString("access_token");
				String usercode = authJsonObj.optString("usercode");
				LOG.debug("HID :  Auth Status = {}", String.valueOf(authStatus));
				if ("ACCEPT".equalsIgnoreCase(authStatus)) {
					LOG.debug("HID : Auth status is accept, returning httpcode 200");
					result.addIntParam(HTTP_STATUS_CODE, 200);
					result.addStringParam(AUTH_STATUS, authStatus);
					result.addStringParam("access_token", accessToken);
					result.addStringParam("usercode", usercode);
				} else if ("DENY".equalsIgnoreCase(authStatus)) {
					LOG.debug("HID : Auth status is deny, returning httpcode 401");
					result.addIntParam(HTTP_STATUS_CODE, 401);
					result.addStringParam(ERR_MSG, "HID ActivID Push based operation approval denied");
					result.addStringParam(AUTH_STATUS, authStatus);
					response.setStatusCode(401);
				} else {
					LOG.debug("HID : Auth status is unknown, returning httpcode 401");
					result.addIntParam(HTTP_STATUS_CODE, 401);
					result.addStringParam(ERR_MSG, "HID ActivID Push based operation approval is not known");
					result.addStringParam(AUTH_STATUS, "UNKNOWN");
					result.addOpstatusParam(-1);
					response.setStatusCode(401);
				}
			} else {
				result.addIntParam(HTTP_STATUS_CODE, 401);
				result.addStringParam(ERR_MSG, "HID ActivID Push based operation approval is not known");
				result.addOpstatusParam(-1);
				result.addStringParam(AUTH_STATUS, "UNKNOWN");
				response.setStatusCode(401);
			}
		} else {
			LOG.error("HID : Input auth request id is null");
			result.addIntParam(HTTP_STATUS_CODE, 400);
			result.addOpstatusParam(-1);
			response.setStatusCode(400);
		}
		return result;
	}

}
