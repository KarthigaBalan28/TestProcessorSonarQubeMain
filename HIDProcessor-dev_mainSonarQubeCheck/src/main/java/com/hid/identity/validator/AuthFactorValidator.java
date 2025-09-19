package com.hid.identity.validator;

import java.util.HashMap;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

@SuppressWarnings({"java:S3740" , "java:S1319"})
public interface AuthFactorValidator {
      public boolean validate(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
  			Result result);
}
