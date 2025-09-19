package com.hid.identity.factory;

import java.util.HashMap;

import com.hid.identity.service.CustomIdentityService;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

@SuppressWarnings({"java:S3740", "java:S1319"})
public interface IdentityServiceFactory {
     public CustomIdentityService getIdentity(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
 			Result result, int factor);
}
