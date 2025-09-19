package com.hid.idv.service;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hid.idv.utils.IDVConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;

public class GetStatusLoopingPostProcessor implements DataPostProcessor2 {

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		try {
			String objStr = ResultToJSON.convert(result);
			JSONObject json = new JSONObject(objStr);
			JSONArray jsonArray = json.optJSONArray(IDVConstants.STATUS_LOOPING_PARAM);
			if (jsonArray == null || jsonArray.length() == 0) {
				setErrorToResult(result, IDVConstants.ERROR_STATUS_FAILED);
				return result;
			}
			int n = jsonArray.length();
			JSONObject lastStatus = jsonArray.getJSONObject(n - 1);
			String status = lastStatus.optString(IDVConstants.REQ_STATUS_PARAM_NAME, "");
			String transactionID = lastStatus.optString(IDVConstants.TRANSACTIONID_PARAM_NAME,"");
			if (status.isEmpty()) {
				setErrorToResult(result, IDVConstants.ERROR_STATUS_FAILED);
				return result;
			}
			result.addStringParam(IDVConstants.REQ_STATUS_PARAM_NAME, status);
			if (!status.contentEquals(IDVConstants.REQ_STATUS_SUCCESS)) {				
				if(status.contentEquals(IDVConstants.REQ_STATUS_EXPIRED)) {
					result.addOpstatusParam(-1);
					result.addErrMsgParam(IDVConstants.ERROR_STATUS_EXPIRED);
					return result;
				} else {
				result.addOpstatusParam(-1);
				result.addErrMsgParam(IDVConstants.ERROR_PENDING_STATUS);
				return result;
				}
			}
			String imageFront = lastStatus.optString(IDVConstants.IMAGE_FRONT_PARAM_NAME,"");
			String imageSelfie = lastStatus.optString(IDVConstants.IMAGE_SELFIE_PARAM_NAME,"");
			String surName = lastStatus.optString(IDVConstants.SURNAME_PARAM_NAME,"");
			String firstName = lastStatus.optString(IDVConstants.FIRSTNAME_PARAM_NAME,"");
			String givenName = lastStatus.optString(IDVConstants.GIVENNAME_PARAM_NAME,"");
			String issuerName = lastStatus.optString(IDVConstants.ISSUERNAME_PARAM_NAME,"");
			String birthDate = lastStatus.optString(IDVConstants.BIRTHDATE_PARAM_NAME,"");
			String actionMessage = lastStatus.optString(IDVConstants.ACTIONMESSAGE_PARAM_NAME,"");
			String transactionStatus = lastStatus.optString(IDVConstants.TRANSCATIONSTATUS_PARAM_NAME,"");
			String code = lastStatus.optString(IDVConstants.CODE_PARAM_NAME, "");
			String sex = lastStatus.optString(IDVConstants.SEX_PARAM_NAME, "");
			String issueDate = lastStatus.optString(IDVConstants.ISSUEDATE_PARAM_NAME, "");
			String documentNumber = lastStatus.optString(IDVConstants.DOCUMENTNUMBER_PARAM_NAME, "");
			String documentIssuerCountry = lastStatus.optString(IDVConstants.DOCUMENTISSUERCOUNTRY_PARAM_NAME, "");
			String classificationType = lastStatus.optString(IDVConstants.CLASSIFICATIONTYPE_PARAM_NAME, "");
			String expirationDate = lastStatus.optString(IDVConstants.EXPIRATIONDATE_PARAM_NAME, "");
			String documentClassName = lastStatus.optString(IDVConstants.DOCUMENTCLASSNAME_PARAM_NAME, "");
			String fullName = lastStatus.optString(IDVConstants.FULLNAME_PARAM_NAME, "");
			String documentIssuerCountryCode = lastStatus.optString(IDVConstants.DOCUMENTISSUERCOUNTRYCODE_PARAM_NAME, "");
			result.addStringParam(IDVConstants.TRANSACTIONID_PARAM_NAME, transactionID);	
			result.addStringParam(IDVConstants.IMAGE_FRONT_PARAM_NAME, imageFront);
			result.addStringParam(IDVConstants.IMAGE_SELFIE_PARAM_NAME, imageSelfie);
			result.addStringParam(IDVConstants.SURNAME_PARAM_NAME, surName);
			result.addStringParam(IDVConstants.FIRSTNAME_PARAM_NAME, firstName);
			result.addStringParam(IDVConstants.GIVENNAME_PARAM_NAME, givenName);
			result.addStringParam(IDVConstants.ISSUERNAME_PARAM_NAME, issuerName);
			result.addStringParam(IDVConstants.BIRTHDATE_PARAM_NAME, birthDate);
			result.addStringParam(IDVConstants.ACTIONMESSAGE_PARAM_NAME, actionMessage);
			result.addStringParam(IDVConstants.TRANSCATIONSTATUS_PARAM_NAME, transactionStatus);
			result.addStringParam(IDVConstants.CODE_PARAM_NAME, code);
			result.addStringParam(IDVConstants.SEX_PARAM_NAME, sex);
			result.addStringParam(IDVConstants.ISSUEDATE_PARAM_NAME, issueDate);
			result.addStringParam(IDVConstants.DOCUMENTNUMBER_PARAM_NAME, documentNumber);
			result.addStringParam(IDVConstants.DOCUMENTISSUERCOUNTRY_PARAM_NAME, documentIssuerCountry);
			result.addStringParam(IDVConstants.CLASSIFICATIONTYPE_PARAM_NAME, classificationType);
			result.addStringParam(IDVConstants.EXPIRATIONDATE_PARAM_NAME, expirationDate);
			result.addStringParam(IDVConstants.DOCUMENTCLASSNAME_PARAM_NAME, documentClassName);
			result.addStringParam(IDVConstants.FULLNAME_PARAM_NAME, fullName);
			result.addStringParam(IDVConstants.DOCUMENTISSUERCOUNTRYCODE_PARAM_NAME, documentIssuerCountryCode);
			
			result.removeParamByName("errmsg");
			result.addOpstatusParam(0);
			result.addHttpStatusCodeParam(200);
			return result;
		}catch (Exception e){
			setErrorToResult(result, e.getLocalizedMessage());
			e.printStackTrace();
			return result;
		}
	}

	private void setErrorToResult(Result result, String errorMsg) {
		result.addOpstatusParam(-1);
		result.addHttpStatusCodeParam(403);
		result.addErrMsgParam(errorMsg);		
	}

}
