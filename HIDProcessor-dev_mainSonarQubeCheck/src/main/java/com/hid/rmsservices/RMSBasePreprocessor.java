package com.hid.rmsservices;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;

import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class RMSBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.rmsservices.RMSBasePreprocessor.class);
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		
		UUID uuid = UUID.randomUUID();
		String appActionId = uuid.toString();
        request.setAttribute("appActionId", appActionId);
        String platform = inputMap.get("platform") != null ? inputMap.get("platform").toString() : "web";
        String applicationId = RMSUtils.getApplicationId(request, platform);
		String channelId = RMSUtils.getChannelId(request, platform);
		String environmentID = GetConfProperties.getProperty(request, AuthenticationConstants.HID_RMS_ENVIRONMENT_ID_KEY);
		String clientIp = RMSUtils.getClientIp(request);
		if(!StringUtils.isEmpty(clientIp)) {
			inputMap.put("client_ip",clientIp);
		}
		LOG.debug("HIDRMS::HIDRMSPayload Server properties applicationId:{} channelId:{} environmentID:{}", applicationId, channelId, environmentID);
		if (!applicationId.isEmpty() && !channelId.isEmpty() && !environmentID.isEmpty()) {
			inputMap.put("application_id", applicationId);
			inputMap.put("channel_id", channelId);
			inputMap.put("app_action_id", appActionId);
			inputMap.put("environment_id",environmentID);
			return true;
		}
		setErrorToResult(result, AuthenticationConstants.EMPTY_APP_CHANNEL_ID, -1, 401);
		return false;
	}
	
	private void setErrorToResult(Result result, String errmsg, int opstatus, int code) {
		result.addOpstatusParam(opstatus);
		result.addErrMsgParam(errmsg);
		result.addHttpStatusCodeParam(code);
		result.addStringParam("errMsg", errmsg);
	}
}
