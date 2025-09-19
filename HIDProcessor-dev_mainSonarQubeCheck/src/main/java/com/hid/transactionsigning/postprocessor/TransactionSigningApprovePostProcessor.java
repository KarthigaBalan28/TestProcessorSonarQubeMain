package com.hid.transactionsigning.postprocessor;

import org.apache.commons.lang3.StringUtils;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class TransactionSigningApprovePostProcessor implements DataPostProcessor2 {

	private static final String ERR_MSG_PARAM = "errmsg";

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		String authReqId = result.getParamValueByName("auth_req_id");
		if (StringUtils.isNotBlank(authReqId)) {
			String clientNotificationToken = request.getAttribute("client_notification_token");
			if (StringUtils.isNotBlank(clientNotificationToken)) {
				request.getServicesManager().getResultCache().insertIntoCache(clientNotificationToken, "CCNT");
			}
		} else if (StringUtils.isNotBlank(result.getParamValueByName(ERR_MSG_PARAM))) {
			result.addStringParam(ERR_MSG_PARAM, result.getParamValueByName(ERR_MSG_PARAM));
			result.addStringParam("httpstatus", result.getParamValueByName("httpstatus"));
			return result;
		}
		return result;
	}

}
