package com.hid.common;

import org.apache.logging.log4j.Logger; 
import org.apache.logging.log4j.LogManager;

import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.URLProvider;
import com.konylabs.middleware.controller.DataControllerRequest;

public class ServiceUrlProvider implements URLProvider {
	private static final Logger LOG = LogManager.getLogger(com.hid.common.ServiceUrlProvider.class);
	@SuppressWarnings("java:S112")
	@Override
	public String execute(String operationURL, DataControllerRequest request) {
		try {
			LOG.debug("HID::In ServiceUrlProvider");
			LOG.debug("HID::ServiceUrlProvider, operation URL is : {}", operationURL);
			String targetURL = "";
			String[] splittedUrl = operationURL.split("__");

			for (String string : splittedUrl) {
				String appendValue = string;
				if (string.startsWith("$")) {
					String propKey = string.substring(1);
					LOG.debug("HID::ServiceUrlProvider, property key is : {}", propKey);
					String propValue = GetConfProperties.getProperty(request, propKey);
					LOG.debug("HID::ServiceUrlProvider, property Value is : {}", propValue);
					if (propValue.isEmpty()) {
						throw new Exception(propKey + " is not configured in server properties");
					}
					appendValue = propValue;
				}
				LOG.debug("HID::ServiceUrlProvider, final operation url is : {}", targetURL);
				targetURL = new StringBuilder(targetURL).append(appendValue).toString();
			}
			return targetURL;
		} catch (Exception e) {
			LOG.error("HID::ServiceUrlProvider, exception caught", e);
		}
		return null;
	}
}