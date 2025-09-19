package com.hid.usermanagement.postprocessor;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger; 
import org.apache.logging.log4j.LogManager;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
@SuppressWarnings({"java:S3776" , "java:S2629","java:S1125"})
public class SearchUserPostProcessor implements DataPostProcessor2 {
	
	private static final Logger LOG = LogManager.getLogger(com.hid.usermanagement.postprocessor.SearchUserPostProcessor.class);
	private static final String USER_EXISTS = "userExists";
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response) 
			throws Exception {
		LOG.debug("HID : In SearchUserPostProcessor");
		String totalResults = result.getParamValueByName("totalResults");
		String authType = request.getAttribute("authType");
		String active = result.getParamValueByName("active");
		String userid = (result.getParamValueByName("UserId") == null) ? "" :result.getParamValueByName("UserId") ;
		LOG.debug(String.format("HID : Values of totalResults = %1$s, active = %2$s and userId = %3$s", totalResults, active, userid));
		request.setAttribute("userId",userid);
		
		LOG.debug("HID : Value of auth type is = {}", authType);
		if(totalResults == null || totalResults.isEmpty()) {
			LOG.debug("HID : TotalResults are null, setting userExists to false");
			request.setAttribute(USER_EXISTS, false);
			return result;
		}else if(StringUtils.isEmpty(active)){
			LOG.debug("HID : Active is null, setting userExists to false");
			request.setAttribute(USER_EXISTS, false);
			return result;
		}else if(!active.equals("true")){
			LOG.debug("HID : active is false, setting userExists to false");
			request.setAttribute(USER_EXISTS, false);
			return result;
		}else {
			int t = Integer.parseInt(totalResults);
			if(t==0) {
				LOG.debug("HID : TotalResults = 0, setting userExists to false");
				request.setAttribute(USER_EXISTS, false);
				return result;
			}
		}
		Dataset authenticators = result.getDatasetById("authenticators");
		request.setAttribute("AuthExists", false);
		if(authenticators != null && !authType.isEmpty()) {
			boolean value = authenticators.getAllRecords() == null ? true : authenticators.getAllRecords().isEmpty();
			if(!value) {
				for(Record r : authenticators.getAllRecords()) {
					Param auth = r.getParam("display");
					if(auth.getValue().equals(authType)) {
						LOG.debug("HID : Authenticator {} exists for user, setting AuthExists to true", authType);
						request.setAttribute("AuthExists", true);
						break;
					}
				}
			}
		}
		request.setAttribute("authenticators", authenticators);
		return result;

	}

}
