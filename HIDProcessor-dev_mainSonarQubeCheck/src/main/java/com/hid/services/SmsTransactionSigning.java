package com.hid.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;

import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
@SuppressWarnings({"java:S4719" , "java:S4087"})
public class SmsTransactionSigning implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(SmsTransactionSigning.class);
	private static final String OPSTATUS = "opstatus";
	private static final String HTTP_STATUS_CODE = "httpStatusCode";
	private static final String DETAIL = "detail";
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		LOG.debug("HID :  In DeviceProvision Java service");
		URL url;
		HttpURLConnection conn = null;
		BufferedReader rd;
		String line;
		StringBuilder result = new StringBuilder();
		String host = GetConfProperties.getProperty(request, AuthenticationConstants.HID_HOST_KEY);
		String tenant = GetConfProperties.getProperty(request, AuthenticationConstants.HID_TENANT_KEY);
		String channelId = GetConfProperties.getProperty(request, AuthenticationConstants.HID_IDP_CHANNEL_ENV_VARIABLE_KEY);
		LOG.debug("HID : Value of HOST is : {} and tenant is : {} from server settings", host, tenant);

		if (channelId.isEmpty()) {
			LOG.error("HID : Server settings for channelId is not configured");
			Result res = new Result();
			res.addIntParam(OPSTATUS, 8007);
			res.addIntParam(HTTP_STATUS_CODE, 400);
			res.addStringParam(DETAIL, "Please configure channelId in settings");
			return res;
		}

		if (host.isEmpty() || tenant.isEmpty()) {
			LOG.error("HID : Server settings for HOST and TENANT are not configured");
			Result res = new Result();
			res.addIntParam(OPSTATUS, 8007);
			res.addIntParam(HTTP_STATUS_CODE, 400);
			res.addStringParam(DETAIL, "Please configure Hostname and tenant in settings");
			return res;
		}

		String callbackUrl = GetConfProperties.getProperty(request, AuthenticationConstants.HID_SERVICES_URL);
		LOG.debug("HID : Value of HID_SERVICES_URL is : {} from server settings", callbackUrl);
		if (callbackUrl.isEmpty()) {
			LOG.error("HID : Server settings for HID_SERVICES_URL are not configured");
			Result res = new Result();
			res.addIntParam(OPSTATUS, 8007);
			res.addIntParam(HTTP_STATUS_CODE, 400);
			res.addStringParam(DETAIL, "Please configure HID_SERVICES_URL in settings");
			return res;
		}
		url = new URL("https://" + host + "/idp/" + tenant + "/authn/token");
		conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Accept", "application/x-www-form-urlencoded");
		String authorizationToken = Objects
				.toString(request.getServicesManager().getResultCache().retrieveFromCache("HIDClientAuthToken"), "");
		authorizationToken = "Bearer " + authorizationToken;
		conn.setRequestProperty("Authorization", authorizationToken);
		String username = Objects.toString(((Map<?, ?>) inputArray[1]).get("username"), null);
		String password = Objects.toString(((Map<?, ?>) inputArray[1]).get("password"), null);
		String correlationId = Objects.toString(((Map<?, ?>) inputArray[1]).get("correlationId"), "");
		String txId = Objects.toString(((Map<?, ?>) inputArray[1]).get("txId"), "");
		conn.setRequestProperty("X-Correlation-ID", correlationId);
		

		String smsValidationBody = "grant_type=password&username=" + username + "&password=" + password
				+ "&context=correlationId:" + correlationId + ":false+txID:" + txId + ":false&channel=" + channelId
				+ "&authType=AT_TXOOB";

		LOG.debug("HID : SMS Transaction Validation request : {}", smsValidationBody);
		try (OutputStream os = conn.getOutputStream()) {
			byte[] input = smsValidationBody.getBytes("utf-8");
			os.write(input, 0, input.length);
			os.close();
		}

		int statusCode = conn.getResponseCode();
		if (statusCode >= 200 && statusCode < 400) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result.append(line);

			}
			rd.close();
			conn.disconnect();
			JSONObject authJsonObj = new JSONObject(result.toString());
			Result res = new Result();
			String attribute = authJsonObj.optString("access_token");

			if (StringUtils.isNotBlank(attribute)) {
				LOG.debug("Access Token {}", attribute);
				res.addStringParam("access_token", attribute);
				return res;
			} else {
				res.addIntParam(HTTP_STATUS_CODE, 417);
				res.addIntParam(OPSTATUS, -1);
			}

			return res;
		}
		rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		conn.disconnect();
		JSONObject authJsonObj = new JSONObject(result.toString());
		Result res = new Result();
		res.addIntParam(OPSTATUS, 8007);
		res.addIntParam(HTTP_STATUS_CODE, statusCode);
		res.addStringParam("error_description", authJsonObj.optString("error_description"));
		return res;
	}
}
