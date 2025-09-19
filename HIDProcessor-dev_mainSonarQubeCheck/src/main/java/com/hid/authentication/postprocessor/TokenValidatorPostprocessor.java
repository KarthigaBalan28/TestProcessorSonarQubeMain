package com.hid.authentication.postprocessor;
import org.apache.logging.log4j.Logger; 
import org.apache.logging.log4j.LogManager;

import com.hid.identity.util.JWTValidationManager;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.hid.dataclasses.HIDIntServiceDataclass;
import com.hid.identity.util.IdentityLogger;
import com.hid.identity.util.ServiceUtils;
import com.hid.util.HIDIntegrationServices;

import com.nimbusds.oauth2.sdk.ParseException;

public class TokenValidatorPostprocessor implements DataPostProcessor2{
	private static final Logger LOG = LogManager.getLogger(com.hid.authentication.postprocessor.TokenValidatorPostprocessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		LOG.debug("HID : In TokenValidatorPostprocessor");
		String accessToken = result.getParamValueByName("access_token");
		String idToken = result.getParamValueByName("id_token");
		
		if (!StringUtils.isEmpty(idToken) && !StringUtils.isEmpty(accessToken)) {
			try {
				if (JWTValidationManager.verifyToken(idToken, request, null, result)) {
					result.addOpstatusParam(0);
					result.addHttpStatusCodeParam(200);
					LOG.debug("HID : In TokenValidatorPostprocessor - Token validation done");
					result.addParam("JWTTokenValidation", "Success");
					return result;
				}else {
					Result res = new Result();
					res.addOpstatusParam(-1);
					res.addHttpStatusCodeParam(400);
					LOG.debug("HID : In TokenValidatorPostprocessor - Token validation done");
					res.addParam("JWTTokenValidation", "Failed");
				}
			} catch (ParseException e) {
				LOG.debug("HID : Exception in TokenValidatorPostprocessor service: {}", e.getMessage());
			}
		} 
		return result;
	}
}