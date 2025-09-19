package com.hid.authentication.preprocessor;

import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import com.hid.util.GetConfProperties;
import com.hid.util.HIDFabricConstants;
import com.hid.util.AuthenticationConstants;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class HIDIdentityServicePreProcessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager.getLogger(com.hid.authentication.preprocessor.HIDIdentityServicePreProcessor.class);

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		LOG.debug("HID : Inside the HIDIdentityServicePreProcessor");

		String payload = Objects.toString(inputMap.get("payload"), null);
		String transactionId = "";
		int cacheExpiryValue = 120;
		String cacheExpiryTime = GetConfProperties.getProperty(request, HIDFabricConstants.HID_CACHE_EXPIRY_TIME_IN_SECONDS);		
		if(!cacheExpiryTime.isEmpty()) {
			cacheExpiryValue = Integer.parseInt(cacheExpiryTime);
		}
		if(!StringUtils.isEmpty(payload)) {
		JSONObject payloadJson = new JSONObject(payload);
		JSONObject metaLoad = payloadJson.getJSONObject("Meta");
		if (metaLoad != null && metaLoad.has("TransactionId")) {
			transactionId = Objects.toString(metaLoad.get("TransactionId"), null);
			if (StringUtils.isEmpty(transactionId)) {
				LOG.debug("HID::HIDIdentityServicePreProcessor ---> Invalid request payload");
				setErrorToResult(result, AuthenticationConstants.INVALID_AUTH_KEY, -1, 401);
				return false;
			}
			request.getServicesManager().getResultCache().insertIntoCache(transactionId, "true", cacheExpiryValue);
		 }
		}
		return true;
	}

	private void setErrorToResult(Result result, String errmsg, int opstatus, int code) {
		result.addOpstatusParam(opstatus);
		result.addErrMsgParam(errmsg);
		result.addHttpStatusCodeParam(code);
		result.addStringParam("errMsg", errmsg);
	}
}
