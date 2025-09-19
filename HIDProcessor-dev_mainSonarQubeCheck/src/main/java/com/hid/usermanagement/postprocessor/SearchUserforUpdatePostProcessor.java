package com.hid.usermanagement.postprocessor;

import org.apache.logging.log4j.Logger; 
import org.apache.logging.log4j.LogManager;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Result;

public class SearchUserforUpdatePostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager.getLogger(
			com.hid.usermanagement.postprocessor.SearchUserforUpdatePostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		LOG.debug("HID : In SearchUserforUpdatePostProcessor");
		String totalResults = result.getParamValueByName("totalResults");

		String userid = (result.getParamValueByName("UserId") == null) ? "" : result.getParamValueByName("UserId");
		request.setAttribute("userId", userid);

		if (totalResults == null || totalResults.isEmpty()) {
			LOG.debug(
					"HID : TotalResults are null, setting userExists to false");
			request.setAttribute("userExists", false);
			return result;
		} else {
			request.setAttribute("userExists", true);
		}
		
		Dataset attributes = result.getDatasetById("attributes");
		LOG.debug("HID : value of userid {}", userid);
		LOG.debug("HID : Value of attributes in Searchuserforpdate {}", attributes.getAllRecords());
		request.setAttribute("attributes", attributes);
		return result;
	}
}
