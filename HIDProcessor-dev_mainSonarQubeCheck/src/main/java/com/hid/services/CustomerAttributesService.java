package com.hid.services;

import org.apache.commons.lang3.StringUtils;

import com.hid.infinityconnector.UserAttributesDependencyManager;
import com.hid.infinityconnector.UserAttributesMetaDataClass;
import com.hid.infinityconnector.UserAttributesService;
import com.hid.util.AuthenticationConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;

public class CustomerAttributesService {
   
	public Result populateUserAttributes(Result currentResult,String username, DataControllerRequest request){
		UserAttributesService uaService = UserAttributesDependencyManager.getAttributesConnector();
		Result userAttrResult = uaService.getUserAttributes(username, request);
		updateResult(currentResult, userAttrResult);
		return currentResult;
	}
	
	private void updateErrorToResult(Result result, String errmsg) {
		result.addErrMsgParam(errmsg);
		result.addOpstatusParam(AuthenticationConstants.OPSTATUS_CUSTOM_ERROR);
		result.addHttpStatusCodeParam(AuthenticationConstants.HTTP_CODE_UNAUTHORIZED);
	}
	
	private void updateResult(Result currentResult, Result userAttrResult) {
		if(!paramsChecker(currentResult, userAttrResult)) return;
		currentResult.addRecord(userAttrResult.getRecordById(UserAttributesMetaDataClass.userAttParamName));
		currentResult.addRecord(userAttrResult.getRecordById(UserAttributesMetaDataClass.securtiyAttParamName));
	}
	
	private boolean paramsChecker(Result currResult, Result userAttrResult) {
		if(userAttrResult == null) {
			updateErrorToResult(currResult, AuthenticationConstants.USER_ATR_GENERIC_ERROR);
			return false;
		}
		if(userAttrResult.getParamByName(UserAttributesMetaDataClass.ERROR_MESSAGE) != null || userAttrResult.getParamByName(UserAttributesMetaDataClass.DBP_ERROR_CODE_KEY) != null) {
			String errorMsg = userAttrResult.getParamValueByName(UserAttributesMetaDataClass.DBP_ERROR_MESSAGE_KEY);
			if(StringUtils.isEmpty(errorMsg)) {
				errorMsg = AuthenticationConstants.USER_ATR_GENERIC_ERROR;
			}
			updateErrorToResult(userAttrResult, errorMsg);
			return false;
		}
		
		return true;
	}

}
