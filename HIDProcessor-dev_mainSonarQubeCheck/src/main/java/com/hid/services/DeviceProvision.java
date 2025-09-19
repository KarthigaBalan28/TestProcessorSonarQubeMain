package com.hid.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
@SuppressWarnings({"java:S117","java:S2293", "java:S1858", "java:S4719", "java:S4087", "java:S3457", "java:S2629", "java:S1874", "java:S1481"})
public class DeviceProvision implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(com.hid.services.DeviceProvision.class);
	private static final String OPSTATUS = "opstatus";
	private static final String HTTP_STATUS_CODE = "httpStatusCode";
	private static final String DETAIL = "detail";
	private static final String AUTHORIZATION = "Authorization";
	private static final String VALUE = "value";
	
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
		String provHost = GetConfProperties.getProperty(request, AuthenticationConstants.HID_PROVISION_HOST);
		LOG.debug("HID : Value of HOST is : {} and tenant is : {} from server settings", host, tenant);

		if (host.isEmpty() || tenant.isEmpty()) {
			LOG.error("HID : Server settings for HID_HOST and HID_TENANT are not configured");
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
		String deviceRegisterCallbackUrl = callbackUrl + "/DeviceRegistrationCallBackEndpoint";
		LOG.debug("HID : Device Registration callback url is : {}", deviceRegisterCallbackUrl);
		url = new URL("https://" + host + "/scim/" + tenant + "/v2/Device/Provision");
		conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/scim+json");
		conn.setRequestProperty("Accept", "application/scim+json");
		String cacheKey = "HIDClientAuthToken";
		String authorization = request.getParameter(AUTHORIZATION) == null ? "" : request.getParameter(AUTHORIZATION).toString();
		conn.setRequestProperty(AUTHORIZATION, authorization);
		String correlationId = request.getParameter("correlationId") == null ? "" : request.getParameter("correlationId").toString();
		conn.setRequestProperty("X-Correlation-ID", correlationId);
		String deviceId = Objects.toString(((Map<?, ?>) inputArray[1]).get("DeviceId"), null);
		String userId = Objects.toString(((Map<?, ?>) inputArray[1]).get("UserId"), null);
		String URL = host + "/" + tenant;
		if(!provHost.isEmpty()) {
			URL =  provHost + "/" + tenant;
		}
		String deviceType = Objects.toString(((Map<?, ?>) inputArray[1]).get("deviceType"), null);
		JSONObject obj = new JSONObject();
		ArrayList<String> arr = new ArrayList<String>();
		arr.add("urn:hid:scim:api:idp:2.0:Provision");
		obj.put("schemas", arr);
		obj.put("deviceType", deviceType);
		obj.put("description",
				"did=" + deviceId + ",url=" + URL + ",pch=CH_TDSPROV,pth=AT_TDSOOB,pct=CT_TDSOOB,pdt=DT_TDSOOB,cb_url="
						+ deviceRegisterCallbackUrl + ",mod=GEN,sec=");
		
		JSONObject owner = new JSONObject();
		owner.put(VALUE, userId);
		obj.put("owner", owner);
		ArrayList<JSONObject> al = new ArrayList<JSONObject>();
		JSONObject attr = new JSONObject();
		attr.put("name", "AUTH_TYPE");
		attr.put(VALUE, "AT_SMK");
		attr.put("readOnly", false);
		al.add(attr);
		obj.put("attributes", al);
		LOG.debug("HID : Device Provisioning request : {}", obj.toString());
		try (OutputStream os = conn.getOutputStream()) {
			byte[] input = obj.toString().getBytes("utf-8");
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
			JSONArray resultAttributes = authJsonObj.optJSONArray("attributes");
			if (resultAttributes != null) {
				JSONObject attribute = resultAttributes.getJSONObject(0);
				res.addStringParam("provisionMsg", attribute.optString(VALUE));
			} else {
				res.addStringParam("provisionMsg", "");
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
		res.addStringParam(DETAIL, authJsonObj.optString(DETAIL));
		return res;
	}

}
