package com.hid.authentication.preprocessor;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
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

public class HIDApprovePreprocessor extends ClientBasePreprocessor implements DataPreProcessor2 {
	private static final Logger LOG = LogManager.getLogger(com.hid.authentication.preprocessor.HIDApprovePreprocessor.class);
	
	@SuppressWarnings("java:S5659")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		if(super.execute(inputMap, request, response, result)) {
		  String username = Objects.toString(inputMap.get("username"),"");
		  String defaultTds = "Please validate logon";
		  String tds = Objects.toString(inputMap.get("pushMsg"), defaultTds);
		  String deviceId = "";
		  if(inputMap.containsKey("deviceId")) {
			  LOG.debug("HID::HIDApprovePreprocessor ---> DeviceId Key Present");
		      deviceId = Objects.toString(inputMap.get("deviceId"),"");
		  } 
		  String authPolicy = AuthenticationConstants.HID_PUSH_LOGON_AUTH_TYPE;
		  String channel = AuthenticationConstants.HID_PUSH_LOG_CHANNEL;
	      Claims claims = Jwts.claims();	      
	      
	      claims.put("authpol", GetConfProperties.getProperty(request, authPolicy));
	      claims.put("tds", "Hello " + username + "\r\n"+ tds);
	      claims.put("channel", GetConfProperties.getProperty(request, channel));
	      claims.put("usercode", username);
	      claims.put("createSession", "1");
	      if(!StringUtils.isEmpty(deviceId)) {
	    	  claims.put("deviceid",deviceId);
	      }
	      String jwt = Jwts.builder().setClaims(claims).compact();
	      inputMap.put("login_hint_token", jwt);
	      String uuid = UUID.randomUUID().toString();
	      inputMap.put("client_notification_token", uuid);
	      
	      //Adding client_notification_token to the request to be used
	      //by post processor to save it in cache
	      request.setAttribute("client_notification_token", uuid);
	      return true;
		}
		return false;
	}
}
