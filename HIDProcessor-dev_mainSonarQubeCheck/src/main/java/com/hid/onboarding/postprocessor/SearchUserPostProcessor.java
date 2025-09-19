package com.hid.onboarding.postprocessor;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class SearchUserPostProcessor implements DataPostProcessor2 {
	
	private static final Logger LOG = LogManager.getLogger(com.hid.onboarding.postprocessor.SearchUserPostProcessor.class);
	
	private static final String USER_EXISTS_PARAM ="userExists";

	@SuppressWarnings({"java:S3457","java:S2629","java:S3776","java:S1125"})
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response) 
			throws Exception {
		LOG.debug("HID : In SearchUserPostProcessor");
		String totalResults = result.getParamValueByName("totalResults");
		String active = result.getParamValueByName("active");
		String userid = (result.getParamValueByName("userId") == null) ? "" :result.getParamValueByName("userId") ;
		LOG.debug(String.format("HID : Values of totalResults = {}, active = {} and userId = {}", totalResults, active, userid));
		request.setAttribute("userid",userid);
		String authType = GetConfProperties.getProperty(request, AuthenticationConstants.HID_ACTIVATION_CODE_AUTHTYPE);
		LOG.debug("HID : Value of ACTIVATION_CODE_AUTHTYPE from settings is = {}", authType);
		LOG.debug("HID : Value of activation auth type is = {}", authType);
		if(totalResults == null || totalResults.isEmpty()) {
			LOG.debug("HID : TotalResults are null, setting userExists to false");
			request.setAttribute(USER_EXISTS_PARAM, false);
			return result;
		}else if(StringUtils.isEmpty(active)){
			LOG.debug("HID : Active is null, setting userExists to false");
			request.setAttribute(USER_EXISTS_PARAM, false);
			return result;
		}else if(!active.equals("true")){
			LOG.debug("HID : active is false, setting userExists to false");
			request.setAttribute(USER_EXISTS_PARAM, false);
			return result;
		}else {
			int t = Integer.parseInt(totalResults);
			if(t==0) {
				LOG.debug("HID : TotalResults = 0, setting userExists to false");
				request.setAttribute(USER_EXISTS_PARAM, false);
				return result;
			}
		}
		Dataset authenticators = result.getDatasetById("authenticators");
		request.setAttribute("AuthExists", false);
		if(authenticators != null) {
			boolean value = authenticators.getAllRecords() == null ? true : authenticators.getAllRecords().isEmpty();
			if(!value) {
				for(Record r : authenticators.getAllRecords()) {
					Param auth = r.getParam("display");
					if(auth.getValue().equals(authType)) {
						LOG.debug("HID : Authenticator "+ authType+" exists for user, setting AuthExists to true" );
						request.setAttribute("AuthExists", true);
						break;
					}
				}
			}
		}
		return result;

	}

}
