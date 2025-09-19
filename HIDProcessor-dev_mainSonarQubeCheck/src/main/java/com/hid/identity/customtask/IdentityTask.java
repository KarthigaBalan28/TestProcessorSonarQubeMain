package com.hid.identity.customtask;

import java.util.HashMap;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

@SuppressWarnings({"java:S3740", "java:S1319"})
public interface IdentityTask {
     public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result);
}
