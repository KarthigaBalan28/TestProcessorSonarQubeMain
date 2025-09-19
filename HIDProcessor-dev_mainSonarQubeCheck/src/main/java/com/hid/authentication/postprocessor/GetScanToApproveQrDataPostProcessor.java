package com.hid.authentication.postprocessor;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;


public class GetScanToApproveQrDataPostProcessor implements DataPostProcessor2 {
	

    private static final Logger LOG = LogManager.getLogger(GetScanToApproveQrDataPostProcessor.class);
	private static final String ERROR_PARAM = "errmsg";
    private static final String HTTP_STATUS_PARAM = "httpstatus";
	
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
			
			String errorMsg = result.getParamValueByName(ERROR_PARAM);

			if (StringUtils.isNotBlank(errorMsg)) {
			LOG.debug("Error message is : {}", errorMsg);
			result.addStringParam(ERROR_PARAM, errorMsg);
			result.addStringParam(HTTP_STATUS_PARAM , result.getParamValueByName(HTTP_STATUS_PARAM));
			return result;
		}
			
		return result;
	}
}
