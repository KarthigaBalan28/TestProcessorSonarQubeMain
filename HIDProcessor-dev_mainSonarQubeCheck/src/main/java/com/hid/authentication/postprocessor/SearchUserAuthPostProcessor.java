package com.hid.authentication.postprocessor;

import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class SearchUserAuthPostProcessor implements DataPostProcessor2 {
	
	private static final Logger LOG = LogManager.getLogger(com.hid.authentication.postprocessor.SearchUserAuthPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : In SearchUserAuthPostProcessor");
		String totalResults = result.getParamValueByName("totalResults") == null ? "" : result.getParamValueByName("totalResults");
		String userid = result.getParamValueByName("id") == null ? "" : result.getParamValueByName("id") ;		
		if(!totalResults.isEmpty()) {
			int totalresult = Integer.parseInt(totalResults);
			if(totalresult != 0 && !userid.isEmpty()) {
				request.setAttribute("UserExist", true);
				LOG.debug("HID : UserExists, setting value true");
			} else {
				request.setAttribute("UserExist", false);
			}			
		}
		return result;
	}
}
