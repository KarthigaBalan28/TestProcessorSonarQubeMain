package com.hid.services;

import java.io.IOException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.api.ServicesManagerHelper;
import com.konylabs.middleware.exceptions.MiddlewareException;
import com.konylabs.middleware.servlet.IntegrationCustomServlet;
@SuppressWarnings({"java:S1161","java:S1141", "java:S1989", "java:S2629", "java:S3457"})
@IntegrationCustomServlet(urlPatterns = "DeviceRegistrationCallBackEndpoint")
public class DeviceRegistrationCallBackEndpoint extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(com.hid.services.DeviceRegistrationCallBackEndpoint.class);

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOG.debug("HID : In DeviceRegistrationCallBackEndpoint");
		try {
			String contentType = req.getContentType();
			LOG.debug("HID : In DeviceRegistrationCallBackEndpoint contentType: {}", contentType);
			JSONObject requestObj;
			StringBuilder sb = new StringBuilder();
			BufferedReader reader = req.getReader();
			String value;
			while ((value = reader.readLine()) != null) {
				sb.append(value);
			}
			String statusJson = sb.toString();
			LOG.debug("HID : Device Registration CallBack Endpoint result == {}", statusJson);
			requestObj = new JSONObject(statusJson);

			LOG.debug("HID : Device Registration callback request == {}", requestObj.toString());
			String username = Objects.toString(requestObj.opt("usercode"), "");
			String deviceId = Objects.toString(requestObj.opt("deviceid"), "");
			LOG.debug("HID : Device Registration callback - DeviceId = {} username = {}", deviceId, username);
			JSONObject statusObj = new JSONObject();
			statusObj.put("status", "SUCCESS");
			statusObj.put("username", username);
			ServicesManager smgr = null;
			try {
				smgr = ServicesManagerHelper.getServicesManager(req);
			} catch (MiddlewareException e) {
				LOG.error("HID : Error ocurred while creating service manager from http request", e);
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
			if (smgr != null)
				smgr.getResultCache().insertIntoCache(deviceId, statusObj.toString());
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
