package com.hid.authentication.preprocessor;

import java.util.HashMap;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class GetScanToApproveQrDataPreProcessor extends ClientBasePreprocessor implements DataPreProcessor2{
	
	private static final Logger LOG = LogManager.getLogger(com.hid.authentication.preprocessor.GetScanToApproveQrDataPreProcessor.class);
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if(super.execute(inputMap, request, response, result)) {
		  String defaultTds = "Please validate logon";
		  String authpol = GetConfProperties.getProperty(request, AuthenticationConstants.HID_PUSH_LOGON_AUTH_TYPE);
	      String deviceType =  GetConfProperties.getProperty(request, AuthenticationConstants.HID_DEVICE_TYPE);
		  LOG.debug("HID::GetScanToApproveQrData ---> DeviceId Key Present"); 
	      Claims claims = Jwts.claims();
	      claims.put("authpol", authpol);
	      claims.put("tds", defaultTds);
	      claims.put("device_type", deviceType);
	      claims.put("createSession", "1");
	      claims.put("mode","userid_less");

	      String jwt = Jwts.builder().setClaims(claims).compact(); //NOSONAR
	      LOG.debug("login hint token {}",jwt);
	      inputMap.put("login_hint_token", jwt);
	      
	      //Adding client_notification_token to the request to be used
	      //by post processor to save it in cache
	      return true;
		}
		return false;
	}
}
