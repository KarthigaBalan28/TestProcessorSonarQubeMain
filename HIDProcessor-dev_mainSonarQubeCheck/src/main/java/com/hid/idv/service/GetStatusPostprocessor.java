package com.hid.idv.service;

import com.hid.idv.utils.IDVConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetStatusPostprocessor implements DataPostProcessor2 {

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		String opstatus = result.getOpstatusParamValue();
		if(!opstatus.contentEquals(IDVConstants.SUCCESS_OPSTATUS)) return result;
		String status = result.getParamValueByName(IDVConstants.REQ_STATUS_PARAM_NAME);
		if(status.contentEquals(IDVConstants.REQ_STATUS_PENDING)) {
			result.addOpstatusParam(-1);
			result.addErrMsgParam(IDVConstants.ERROR_PENDING_STATUS);
			return result;
		}else if(status.contentEquals(IDVConstants.REQ_STATUS_FAILED)){
			result.addOpstatusParam(-1);
			result.addErrMsgParam(IDVConstants.ERROR_STATUS_FAILED);
			return result;
		}
		return result;
	}

}
