package com.hid.authentication.postprocessor;

import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetUserIdentityAttributesPostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.authentication.postprocessor.GetUserIdentityAttributesPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : In GetUserIdentityAttributesPostProcessor");
		String opstatus = result.getOpstatusParamValue();		
		LOG.debug("HID : Value of opstatus is : {}", opstatus);
		int os = Integer.parseInt(opstatus);
		if (os != 0) {
			LOG.debug("HID : opstatus of GetUserIdentityAttribute service is {}", os);			
			request.setAttribute("sequenceFailed", true);
			request.setAttribute("errMessage", "Failed to get user identity attributes");
			return result;
		}
		return result;
	}
}
