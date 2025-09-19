package com.hid.util;

import com.konylabs.middleware.api.ConfigurableParametersHelper;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.controller.DataControllerRequest;
@SuppressWarnings({"java:S1118", "java:S1488", "java:S112"})
public class GetConfProperties {
   public static final String getProperty(DataControllerRequest request, String name) throws Exception{
	   ServicesManager sm = request.getServicesManager();
	   ConfigurableParametersHelper paramHelper = sm.getConfigurableParametersHelper();
	   String property = paramHelper.getServerProperty(name) == null ? "":paramHelper.getServerProperty(name);
	   return property;
   }
}
