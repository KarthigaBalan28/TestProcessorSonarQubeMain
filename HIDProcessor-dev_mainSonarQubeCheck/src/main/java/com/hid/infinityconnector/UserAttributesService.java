package com.hid.infinityconnector;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public interface UserAttributesService {
    Result getUserAttributes(String userName, DataControllerRequest request);
}
