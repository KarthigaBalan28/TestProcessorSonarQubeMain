package com.hid.authentication.postprocessor;

import org.apache.commons.lang3.StringUtils;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class HIDApprovePostprocessor implements DataPostProcessor2 {
	
	private static final String ERROR_PARAM = "errmsg";
    private static final String HTTP_STATUS_PARAM = "httpstatus";

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		String authReqId = result.getParamValueByName("auth_req_id");
		if (StringUtils.isNotBlank(authReqId)) {
			String clientNotificationToken = request.getAttribute("client_notification_token");
			if (StringUtils.isNotBlank(clientNotificationToken)) {
				request.getServicesManager().getResultCache().insertIntoCache(clientNotificationToken, "CCNT");
			}
		} else if (StringUtils.isNotBlank(result.getParamValueByName(ERROR_PARAM))) {
			result.addStringParam(ERROR_PARAM, result.getParamValueByName(ERROR_PARAM));
			result.addStringParam(HTTP_STATUS_PARAM, result.getParamValueByName(HTTP_STATUS_PARAM));
			return result;
		}
		return result;
	}

}
