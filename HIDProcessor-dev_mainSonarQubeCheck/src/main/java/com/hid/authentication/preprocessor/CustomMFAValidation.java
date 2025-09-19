package com.hid.authentication.preprocessor;


import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;

import com.hid.common.ClientBasePreprocessor;
import com.hid.dataclasses.HIDIntServiceDataclass;
import com.hid.dataclasses.HIDRMSDataclass;
import com.hid.rmsservices.LoginRMSApi;
import com.hid.rmsservices.RMSUtils;
import com.hid.services.CustomerAttributesService;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.hid.util.HIDIntegrationServices;

import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;


public class CustomMFAValidation extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager.getLogger(com.hid.authentication.preprocessor.CustomMFAValidation.class);

	private static final String USERNAME_PARAM = "username";
	private static final String TM_ACTION_ID = "tm_action_id";
	private static final String APPROVE_PARAM = "APPROVE";
	private static final String SUCCESS_PARAM = "success";

	@SuppressWarnings({"java:S1192","java:S1854","java:S1481"})
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,  //NOSONAR
			Result result) throws Exception {
		LOG.debug("HID::CustomMFAValidation ---> Started Execute");
		try {
			if (super.execute(inputMap, request, response, result)) {
				HashMap<String, Object> bodyMap = new HashMap<>();
				HashMap<String, Object> headerMap = new HashMap<>();
				String mfaKey = Objects.toString(inputMap.get("mfa_key"), null);
				String password = Objects.toString(inputMap.get("password"), null);
				String authType = Objects.toString(inputMap.get("authType"), null);
				String authJsonString = Objects
						.toString(request.getServicesManager().getResultCache().retrieveFromCache(mfaKey), null);
				LOG.debug("HID :: CustomMFAValidation {} {}", authType, mfaKey);
				
				if (StringUtils.isEmpty(authJsonString)) {
					LOG.debug("HID::CustomMFAValidation ---> mfa_key not found in cache");
					setErrorToResult(result, request, AuthenticationConstants.FIRST_FACTOR_NOT_AUTHENTICATED, -1, 401);
					return false;
				}
				JSONObject authJsonObj = new JSONObject(authJsonString);
				String username = authJsonObj.optString(USERNAME_PARAM, "");
				request.setAttribute(USERNAME_PARAM, username);
				String accessToken = authJsonObj.optString("Bearer_token", "");
				String appSessionId = authJsonObj.optString("appSessionId", "");
				String platform = authJsonObj.optString("platform", "");
				String tmSessionId = authJsonObj.optString("tmSessionId", "");
				String tmDeviceTag = authJsonObj.optString("tmDeviceTag", "");
				String clientIp = authJsonObj.optString("clientIp", "");
				String tmActionId = authJsonObj.optString(TM_ACTION_ID,"");
				String applicationId = "";
				String channelId = "";
				String environmentId = "";
				HIDRMSDataclass hidrms = null;
				
				LOG.debug("HID :: CustomMFAValidation {} {} {} {} {} {} {}" , appSessionId , username , accessToken , appSessionId ,
						tmSessionId , tmDeviceTag , clientIp);
				boolean isRMSEnabled = false;
				if (!appSessionId.isEmpty()) {
					environmentId = GetConfProperties.getProperty(request, AuthenticationConstants.HID_RMS_ENVIRONMENT_ID_KEY);
					applicationId = RMSUtils.getApplicationId(request, platform);
					channelId = RMSUtils.getChannelId(request, platform);
					isRMSEnabled = true;
					int loginFactorIndex = 1;
					String securityItemType = AuthenticationConstants.RMS_APPROVE_SECURITY_ITEM_TYPE;
					hidrms = new HIDRMSDataclass(applicationId, channelId, environmentId,tmDeviceTag, tmSessionId, clientIp,
							loginFactorIndex, securityItemType, appSessionId, APPROVE_PARAM);
					LOG.debug("HIDRMS :: CustomMFAValidation Username: {} appSessionId: {} isRSEnabled: {}", username, appSessionId, isRMSEnabled);
				}
				if (StringUtils.isEmpty(accessToken)) {
					LOG.debug("HID::CustomMFAValidation ---> Access_token Not found");
					setErrorToResult(result, request, AuthenticationConstants.EMPTY_TOKEN, -1, 401);
					request.getServicesManager().getResultCache().removeFromCache(mfaKey);
					return false;
				}
				if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
					setErrorToResult(result, request, AuthenticationConstants.EMPTY_UN_PWD, -1, 401);
					return false;
				}			
				if ("APPROVE_DENY".equals(authType) || "APPROVE_TIMEOUT".equals(authType)) {
					HIDRMSDataclass hidrmsApprove = new HIDRMSDataclass(applicationId, channelId,environmentId, tmDeviceTag,
							tmSessionId, clientIp, 2, AuthenticationConstants.RMS_APPROVE_SECURITY_ITEM_TYPE,
							appSessionId, APPROVE_PARAM);
					LoginRMSApi loginRMSApi = new LoginRMSApi();
					Result rmsResult = loginRMSApi.getTag(hidrmsApprove, username, request,
							AuthenticationConstants.LOGIN_STEP_DENIED, 2);
					LOG.debug("HIDRMS :: CustomMFAValidation Username:{} appSessionId:{} Approve declined by user /timed out", username, appSessionId);
					setErrorToResult(result, request, "Approve declined by user /timed out", -1, 401);
					return false;
				}
				if ("FIDO".equals(authType)) {
					HIDRMSDataclass hidrmsFido = new HIDRMSDataclass(applicationId, channelId,environmentId, tmDeviceTag,
							tmSessionId, clientIp, 2, AuthenticationConstants.RMS_FIDO_SECURITY_ITEM_TYPE,
							appSessionId, "FIDO");
					LoginRMSApi loginRMSApi = new LoginRMSApi();
					Result rmsResult = loginRMSApi.getTag(hidrmsFido, username, request,
							AuthenticationConstants.LOGIN_STEP_DENIED, 2);
					LOG.debug("HIDRMS :: CustomMFAValidation Username:{} appSessionId:{} Fido declined by user /timed out", username, appSessionId);
					setErrorToResult(result, request, "Approve declined by user /timed out", -1, 401);
					return false;
				}
				if("NO_MFA".equals(authType)) {
					result.addOpstatusParam(0);
					result.addHttpStatusCodeParam(200);
					result.addStringParam(SUCCESS_PARAM, "true");
					request.getServicesManager().getResultCache().removeFromCache(mfaKey);
					updateUserAttributesFromInfinity(result,request,username);
                    return false;					
				}
				if("STEP_DOWN".equals(authType)) {
					LOG.debug("HIDRMS::CustomMFAValidation Username:{} appSessionId:{} ---> STEP_DOWN", username, appSessionId);
					if(!appSessionId.isEmpty()) {
					  LoginRMSApi loginRMSApi = new LoginRMSApi();
					  loginRMSApi.updateSuccessLogin(hidrms, appSessionId, username,tmActionId,request);
					}
					result.addOpstatusParam(0);
					result.addHttpStatusCodeParam(200);
					result.addStringParam(SUCCESS_PARAM, "true");
					request.getServicesManager().getResultCache().removeFromCache(mfaKey);
					updateUserAttributesFromInfinity(result,request,username);
                    return false;					
				}
				if (APPROVE_PARAM.equals(authType)) {		
					LOG.debug("HIDRMS::CustomMFAValidation Username:{} appSessionId:{} ---> APPROVE", username, appSessionId);
					String authString = Objects
							.toString(request.getServicesManager().getResultCache().retrieveFromCache(password), null);
					if (authString != null) {
						result.addOpstatusParam(0);
						result.addHttpStatusCodeParam(200);
						result.addStringParam(SUCCESS_PARAM, "true");
						// RMS Success Call here
						if (isRMSEnabled) {
							HIDRMSDataclass hidrmsApprove = new HIDRMSDataclass(applicationId, channelId,environmentId, tmDeviceTag, tmSessionId, clientIp,
									2, AuthenticationConstants.RMS_APPROVE_SECURITY_ITEM_TYPE, appSessionId, APPROVE_PARAM);
                            LoginRMSApi loginRMSApi = new LoginRMSApi();
                            Result rmsResult = loginRMSApi.getTag(hidrmsApprove, username, request, AuthenticationConstants.LOGIN_STEP_SUCCESS, 2);
                            String tmActionIdFromResp = rmsResult.getParamValueByName(TM_ACTION_ID);
                            if(!StringUtils.isEmpty(tmActionIdFromResp)) {
          					  loginRMSApi.updateSuccessLogin(hidrmsApprove, appSessionId, username,tmActionIdFromResp,request);
                            }
						}
						request.getServicesManager().getResultCache().removeFromCache(mfaKey);
						request.getServicesManager().getResultCache().removeFromCache(password);
						updateUserAttributesFromInfinity(result,request,username);
					} else {
						LOG.debug("HID::CustomMFAValidation Username:{} appSessionId:{} ---> Approve status not known", username, appSessionId);
						setErrorToResult(result, request, AuthenticationConstants.APPROVE_STATUS_NOT_KNOWN, -1, 401);
						if (isRMSEnabled) {
							LOG.debug("HIDRMS : CustomMFAValidation RMS SecondFactor Call ");
                            LoginRMSApi loginRMSApi = new LoginRMSApi();
                            Result rmsResult = loginRMSApi.getTag(hidrms, username, request, AuthenticationConstants.LOGIN_STEP_DENIED, 2);
						}
					}
					return false;
				}
				bodyMap.put(USERNAME_PARAM, username);
				bodyMap.put("password", password);
				bodyMap.put("authType", authType);
				headerMap.put("Authorization", accessToken);
				HIDIntServiceDataclass serviceData = HIDIntegrationServices.getHIDServiceDataObject(authType);
				if (serviceData == null) {
					setErrorToResult(result, request, AuthenticationConstants.INVALID_AUTH_TYPE, -1, 401);
					return false;
				}
				String serviceName = serviceData.getServiceName();
				String operationName = serviceData.getOperationName();
				Result result1 = HIDIntegrationServices.call(serviceName, operationName, request, headerMap, bodyMap);
				String authToken = result1.getParamValueByName("access_token");				
				if (!StringUtils.isEmpty(authToken)) {
					result.addOpstatusParam(0);
					result.addHttpStatusCodeParam(200);
					result.addStringParam(SUCCESS_PARAM, "true");
					request.getServicesManager().getResultCache().removeFromCache(mfaKey);
					// RMS Call when OTP/Secure Code/ Email
					if (isRMSEnabled) {
						LOG.debug("HIDRMS :: CustomMFAValidation RMS SecondFactor Call ");
                        LoginRMSApi loginRMSApi = new LoginRMSApi();
                    	HIDRMSDataclass hidrmsOTP = new HIDRMSDataclass(applicationId, channelId, environmentId,tmDeviceTag, tmSessionId, clientIp,
    							2, AuthenticationConstants.RMS_OTP_SECURITY_ITEM_TYPE, appSessionId, authType);
                        Result rmsResult = loginRMSApi.getTag(hidrmsOTP, username, request, AuthenticationConstants.LOGIN_STEP_SUCCESS, 2);
                        String tmActionIdFromResp = rmsResult.getParamValueByName(TM_ACTION_ID);
						LOG.debug("HIDRMS :: CustomMFAValidation RMS tmActionIdFromResp : {}", tmActionIdFromResp);
                        if(!StringUtils.isEmpty(tmActionIdFromResp)) {
      					  loginRMSApi.updateSuccessLogin(hidrmsOTP, appSessionId, username,tmActionIdFromResp,request);
                        }
					}
					updateUserAttributesFromInfinity(result,request,username);

				} else {
					if (isRMSEnabled) {
						LOG.debug("HIDRMS :: CustomMFAValidation RMS SecondFactor Call ");
                        LoginRMSApi loginRMSApi = new LoginRMSApi();
                        HIDRMSDataclass hidrmsOTP = new HIDRMSDataclass(applicationId, channelId,environmentId, tmDeviceTag, tmSessionId, clientIp,
    							2, AuthenticationConstants.RMS_OTP_SECURITY_ITEM_TYPE, appSessionId, authType);
                        Result rmsResult = loginRMSApi.getTag(hidrmsOTP, username, request, AuthenticationConstants.LOGIN_STEP_INVALID, 2);

					}
					setErrorToResult(result, request, result1.getParamValueByName("errormsg"), -1, 401);
				}
			}
			return false;
		} catch (Exception e) {
			setErrorToResult(result, request, e.getMessage(), -2, 401);
			LOG.error("HID::CustomMFAValidation ---> Exception occured while calling CustomValidation Service {}", e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	private void setErrorToResult(Result result, DataControllerRequest request, String errmsg, int opstatus, int code) {
		LOG.error("HID::CustomMFAValidation ---> Error occurred while validating MFA : {}", errmsg);
		result.addOpstatusParam(opstatus);
		result.addErrMsgParam(errmsg);
		result.addHttpStatusCodeParam(code);		
		request.setAttribute("sequenceFailed", true);
		request.setAttribute("errMessage", "Failed to validate mfa");
	}
	
	private void updateUserAttributesFromInfinity(Result result, DataControllerRequest request,String username) {
		String checkForUserIdentityAttributes = "";
		try {
		   checkForUserIdentityAttributes = GetConfProperties.getProperty(request, AuthenticationConstants.HID_USER_ATR_FLAG);
		} catch (Exception e){
			LOG.error("HID::CustomMFAValidation ---> Exception while fetching property {}", e.getMessage());
		}
		if(!checkForUserIdentityAttributes.isEmpty() && checkForUserIdentityAttributes.equalsIgnoreCase(AuthenticationConstants.USER_ATR_BYPASS)) return;
		CustomerAttributesService caService = new CustomerAttributesService();
		caService.populateUserAttributes(result,username, request);
	}
}
