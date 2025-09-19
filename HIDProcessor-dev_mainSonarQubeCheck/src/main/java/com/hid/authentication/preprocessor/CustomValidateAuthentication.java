package com.hid.authentication.preprocessor;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
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
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

@SuppressWarnings({ "java:S2140", "java:S6541", "java:S3776", "java:S1854" })
public class CustomValidateAuthentication extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager
			.getLogger(com.hid.authentication.preprocessor.CustomValidateAuthentication.class);

	private static final String AUTHORIZATION_PARAM = "Authorization";
	private static final String PASSWORD_PARAM = "password";
	private static final String USERNAME_PARAM = "username";
	private static final String SUCCESS_PARAM = "success";
	private static final String IS_MFA_ENABLED = "is_mfa_enabled";
	private static final String BOOLEAN_PARAM = "boolean";
	private static final String ACCESS_TOKEN_PARAM = "access_token";
	private static final String USER_ATTRIBUTE_PARAM = "user_attributes";
	private static final String USER_ID_PARAM = "user_id";
	private static final String FALSE_PARAM = "false";
	private static final String CURRENT_THREAT_PARAM = "currentThreat";
	private static final String STEP_UP_PARAM = "stepUp";
	private static final String RMS_SERVICE_STATUS_PARAM = "RMSServiceStatus";

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) {
		try {
			if (super.execute(inputMap, request, response, result)) {
				HashMap<String, Object> bodyMap = new HashMap<>();
				HashMap<String, Object> headerMap = new HashMap<>();
				String payload = Objects.toString(inputMap.get("payload"), null);
				String authType = Objects.toString(inputMap.get("authType"), null);
				String isMfa = "";
				try {  //NOSONAR
					isMfa = Objects.toString(GetConfProperties.getProperty(request, AuthenticationConstants.HID_IS_MFA_REQUIRED), "true");
				} catch (Exception e) {			
					e.printStackTrace();
				}
				String bearerToken = request.getParameter(AUTHORIZATION_PARAM) == null ? ""
						: request.getParameter(AUTHORIZATION_PARAM);
				if (StringUtils.isEmpty(payload)) {
					LOG.debug("HID::CustomValidateAuthentication ---> Login payload is Empty");
					setErrorToResult(result, AuthenticationConstants.EMPTY_UN_PWD, -1, 401);
					return false;
				}
				if (StringUtils.isEmpty(bearerToken)) {
					LOG.debug("HID::CustomValidateAuthentication ---> Bearer Token is Empty");
					setErrorToResult(result, AuthenticationConstants.EMPTY_TOKEN, -1, 401);
					return false;
				}
				String username = "";
				String password = "";
				String tmDeviceTag = "";
				String tmSessionId = "";
				String clientIp = "";
				String appSessionId = "";
				String platform = "";
				String applicationId = "";
				String channelId = "";
				String environmentID = "";
				// RMS value initialization...
				HIDRMSDataclass hidrms = null;
				boolean isRMSEnabled = false;
				try {   // NOSONAR
					JSONObject payloadRmsJson = new JSONObject(payload);
					JSONObject metaLoad = payloadRmsJson.getJSONObject("Meta");
					if (metaLoad != null && metaLoad.has("rmspayload")) {
						JSONObject rmsLoad = metaLoad.getJSONObject("rmspayload");
						LOG.debug("HIDRMS::HIDRMSPayload rmsLoad {} {} {}", rmsLoad.getString("tm_tag"), rmsLoad.getString("tm_sid"), rmsLoad.get("app_session_id")); //NOSONAR
						tmDeviceTag = rmsLoad.getString("tm_tag");
						tmSessionId = rmsLoad.getString("tm_sid");
						clientIp = (String) rmsLoad.get("client_ip");
						appSessionId = (String) rmsLoad.get("app_session_id");
						environmentID = GetConfProperties.getProperty(request, AuthenticationConstants.HID_RMS_ENVIRONMENT_ID_KEY);
						platform = rmsLoad.optString("platform","");
						applicationId = RMSUtils.getApplicationId(request, platform);
						channelId = RMSUtils.getChannelId(request, platform);
						LOG.debug("HIDRMS::HIDRMSPayload Server properties {} {}", applicationId, channelId);
						if (applicationId.isEmpty() || channelId.isEmpty() || environmentID.isEmpty()) {
							setErrorToResult(result, AuthenticationConstants.EMPTY_APP_CHANNEL_ID, -1, 401);
							return false;
						}
						LOG.debug("HIDRMS::HIDRMSPayload Server properties {} {}", applicationId, channelId);
						LOG.debug("HIDRMS::HIDRMSPayload {}", rmsLoad);
						isRMSEnabled = true;
						String secureItemType = PASSWORD_PARAM;
						switch(authType) {
						case AuthenticationConstants.SECURE_CODE_KEY:
							secureItemType = "otp";
							break;
						case AuthenticationConstants.SMS_OTP_KEY:
							secureItemType = "otp";
							break;
						case AuthenticationConstants.EMAIL_OTP_KEY:
							secureItemType = "otp";
							break;
						case AuthenticationConstants.FIDO_KEY:
							secureItemType = "fido";
							break;
						case AuthenticationConstants.APPROVE_KEY:
							secureItemType = "pki";
							break;
						default:
							secureItemType = PASSWORD_PARAM;
						}
						hidrms = new HIDRMSDataclass(applicationId, channelId, environmentID, tmDeviceTag, tmSessionId, appSessionId,
								clientIp, secureItemType,"AT_CUSTOTP");
					}
				} catch (Exception e) {
					LOG.error("HID::CustomValidateAuthentication, Error occurred during rms Json Parsing, error is {}", e.getMessage());
				}
				try { // NOSONAR
					JSONObject payloadJson = new JSONObject(payload);
					String eventKey = payloadJson.getJSONObject("Meta").getString("EventType");
					JSONObject loginJson = payloadJson.getJSONObject(eventKey);
					username = loginJson.getString("userid");
					password = loginJson.getString(PASSWORD_PARAM);
					LOG.debug("HID::CustomValidateAuthentication ---> Value of username is : {}", username);
				} catch (Exception e) {
					LOG.error("HID::CustomValidateAuthentication, Error occurred during Json Parsing, error is : {}", e.getMessage());
					setErrorToResult(result, e.getMessage(), -1, 401);
					return false;					
				}
				if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
					LOG.debug("HID::CustomValidateAuthentication ---> Username or password is Empty");
					setErrorToResult(result, AuthenticationConstants.EMPTY_UN_PWD, -1, 401);
					if (isRMSEnabled) {
						String loginStepResult = AuthenticationConstants.LOGIN_STEP_INVALID;
						LoginRMSApi rmsApi = new LoginRMSApi();
						rmsApi.getTag(hidrms, username, request, loginStepResult, 1);
					}
					return false;
				}
				isMfa = StringUtils.isEmpty(isMfa) ? "true" : isMfa;
				bodyMap.put(USERNAME_PARAM, username);
				bodyMap.put(PASSWORD_PARAM, password);
				bodyMap.put("authType", authType);
				headerMap.put(AUTHORIZATION_PARAM, bearerToken);
				LOG.debug("HID::CustomValidateAuthentication ---> authType {}", authType);
				if ("APPROVE".equals(authType)) {
					result.addOpstatusParam(0);
					result.addHttpStatusCodeParam(200);
					result.addStringParam(SUCCESS_PARAM, "true");
					String authString = Objects
							.toString(request.getServicesManager().getResultCache().retrieveFromCache(password), null);
					if (authString != null) {
						if ("true".equalsIgnoreCase(isMfa)) {
							result.addParam(new Param(IS_MFA_ENABLED, "true", BOOLEAN_PARAM));
							JSONObject statusObj = new JSONObject();
							statusObj.put(USERNAME_PARAM, username);
							statusObj.put(ACCESS_TOKEN_PARAM, bearerToken);
							statusObj.put("Bearer_token", bearerToken);
							String authId = getRandomString(8);
							request.getServicesManager().getResultCache().insertIntoCache(authId, statusObj.toString(),
									300);
							Record mfaMeta = new Record();
							mfaMeta.setId("mfa_meta");
							mfaMeta.addStringParam("auth_id", authId);
							Record hidUsrAttr = new Record();
							hidUsrAttr.setId(USER_ATTRIBUTE_PARAM);
							hidUsrAttr.addStringParam(USER_ID_PARAM, bearerToken);
							result.addRecord(mfaMeta);
							result.addRecord(hidUsrAttr);
						} else {
							result.addParam(new Param(IS_MFA_ENABLED, FALSE_PARAM, BOOLEAN_PARAM));
							Record hidUsrAttr = new Record();
							hidUsrAttr.setId(USER_ATTRIBUTE_PARAM);
							hidUsrAttr.addStringParam(USER_ID_PARAM, username);
							result.addRecord(hidUsrAttr);
							Record hidSessionAttr = new Record();
							hidSessionAttr.setId("security_attributes");
							hidSessionAttr.addStringParam("session_token", bearerToken);
							result.addRecord(hidSessionAttr);
						}
						request.getServicesManager().getResultCache().removeFromCache(password);
					} else {
						LOG.debug("HID::CustomValidateAuthentication ---> Approval Status Not Known");
						setErrorToResult(result, AuthenticationConstants.APPROVE_STATUS_NOT_KNOWN, -1, 401);
						if (isRMSEnabled) {
							String loginStepResult = AuthenticationConstants.LOGIN_STEP_DENIED;
							LoginRMSApi rmsApi = new LoginRMSApi();
							rmsApi.getTag(hidrms, username, request, loginStepResult, 1);
						}
						updateUserAttributesFromInfinity(result, request,username);
					}
					return false;
				}
				HIDIntServiceDataclass serviceData = HIDIntegrationServices.getHIDServiceDataObject(authType);
				if (serviceData == null) {
					LOG.error("HID::CustomValidateAuthentication ---> Provided AuthType {} is Invalid", authType);
					setErrorToResult(result, "Invalid Authenticator", -1, 400);
					if (isRMSEnabled) {
						String loginStepResult = AuthenticationConstants.LOGIN_STEP_INVALID;
						LoginRMSApi rmsApi = new LoginRMSApi();
						rmsApi.getTag(hidrms, username, request, loginStepResult, 1);
					}
					return false;
				}
				String serviceName = serviceData.getServiceName();
				String operationName = serviceData.getOperationName();
				LOG.debug("HID::CustomValidateAuthentication ---> {} {}", serviceName , operationName);

				Result result1 = HIDIntegrationServices.call(serviceName, operationName, request, headerMap, bodyMap);
				String accessToken = result1.getParamValueByName(ACCESS_TOKEN_PARAM);
				String idToken = result1.getParamValueByName("id_token");
				idToken = StringUtils.isEmpty(idToken) ? accessToken : idToken;
				LOG.debug("HID::CustomValidateAuthentication ---> {} {} {}", idToken , accessToken, isMfa);
				if (!StringUtils.isEmpty(accessToken)) {
					result.addOpstatusParam(0);
					result.addStringParam(SUCCESS_PARAM, "true");
					if ("true".equalsIgnoreCase(isMfa)) {
						result.addParam(new Param(IS_MFA_ENABLED, "true", BOOLEAN_PARAM));
						JSONObject statusObj = new JSONObject();
						// RMS start
						Record rmsMeta = new Record();
						rmsMeta.setId("rms");
						if (isRMSEnabled) {
							statusObj.put("appSessionId", appSessionId);
							statusObj.put("tmSessionId", hidrms.getTmSessionId());
							statusObj.put("tmDeviceTag", hidrms.getTmDeviceTag());
							statusObj.put("clientIp", hidrms.getClientIp());
							int currentScore = 10;
							try { // NOSONAR
								LOG.debug("HIDRMS :: RMS is enabled");
								String loginStepResult = AuthenticationConstants.LOGIN_STEP_SUCCESS;
								LoginRMSApi loginRMSApi = new LoginRMSApi();
								Result rmsResult = loginRMSApi.getTag(hidrms, username, request, loginStepResult, 1);
								if (rmsResult != null) {
									String stepTag = rmsResult.getParamValueByName("stepTag") == null ? "" : rmsResult.getParamValueByName("stepTag") ;
									String riskScore = rmsResult.getParamValueByName("risk");
									String currentThreat = rmsResult.getParamValueByName(CURRENT_THREAT_PARAM);
									String tmActionIdResp = rmsResult.getParamValueByName("tm_action_id");
									statusObj.put("tm_action_id", tmActionIdResp);
									boolean isSignalDetected = false;
									Dataset tagsDataSet = rmsResult.getDatasetById("tags");
									ArrayList<String> tagsArrayList = new ArrayList<>();
									if (tagsDataSet != null) {
										List<Record> tags = tagsDataSet.getAllRecords();
										boolean isEmptyDataSet = tags == null || tags.isEmpty();
										if(!isEmptyDataSet) {
											for(Record r : tags) {
			                                    tagsArrayList.add(r.getParamValueByName("tagAction"));
											}
										}
									}
									for(String tag :RMSUtils.getSignalTags()) {
										if(tagsArrayList.contains(tag)) {
										  rmsMeta.addStringParam(tag, "true");
										  isSignalDetected = true;
										}
									}
									if ( isSignalDetected || stepTag.equalsIgnoreCase(AuthenticationConstants.RMS_STEP_UP_TAG)) {
										rmsMeta.addStringParam(STEP_UP_PARAM, "true");
										rmsMeta.addStringParam(CURRENT_THREAT_PARAM, currentThreat);
										rmsMeta.addStringParam(RMS_SERVICE_STATUS_PARAM, SUCCESS_PARAM);
										rmsMeta.addStringParam("riskScore", riskScore);
										LOG.debug("HIDRMS :: Step-up is required and having tag as");
									}
									else if ( stepTag.equalsIgnoreCase(AuthenticationConstants.RMS_STEP_DOWN_TAG)){
										rmsMeta.addStringParam(STEP_UP_PARAM, FALSE_PARAM);
										rmsMeta.addStringParam("riskScore", riskScore);
										rmsMeta.addStringParam(CURRENT_THREAT_PARAM, currentThreat);
										rmsMeta.addStringParam(RMS_SERVICE_STATUS_PARAM, SUCCESS_PARAM);
									} 
									else if (stepTag.equalsIgnoreCase(AuthenticationConstants.RMS_BLOCK_TAG)) {
										LOG.debug("HIDRMS :: {} is blocked by RMS for Login attempt, because of miscellaneous Activity", username);
										setErrorToResult(result, AuthenticationConstants.RMS_BLOCK_TAG , -3, 401);
										return false;
									}
									else {
										rmsMeta.addStringParam(STEP_UP_PARAM, "true");
										rmsMeta.addStringParam(RMS_SERVICE_STATUS_PARAM, "failed");
									}
								}else {
									rmsMeta.addStringParam(STEP_UP_PARAM, "true");
									rmsMeta.addStringParam(RMS_SERVICE_STATUS_PARAM, "failed");
									rmsMeta.addStringParam("rms_exception", "RMS Service Failed");
								}
							} catch (Exception e) {
								rmsMeta.addStringParam(STEP_UP_PARAM, "true");
								rmsMeta.addStringParam("rms_exception", e.getMessage());
								rmsMeta.addStringParam("currentRisk", String.valueOf(currentScore));
								LOG.error("HID::CustomValidateAuthentication ---> {}", isRMSEnabled);
							}
						}
						statusObj.put(USERNAME_PARAM, username);
						statusObj.put(ACCESS_TOKEN_PARAM, accessToken);
						statusObj.put("Bearer_token", bearerToken);
						statusObj.put("platform", platform);
						String authId = getRandomString(8);
						request.getServicesManager().getResultCache().insertIntoCache(authId, statusObj.toString(),
								300);
						Record mfaMeta = new Record();
						mfaMeta.setId("mfa_meta");
						mfaMeta.addStringParam("auth_id", authId);
						mfaMeta.addRecord(rmsMeta);
						Record hidUsrAttr = new Record();
						hidUsrAttr.setId(USER_ATTRIBUTE_PARAM);
						hidUsrAttr.addStringParam(USER_ID_PARAM, accessToken);
						result.addRecord(mfaMeta);
						result.addRecord(hidUsrAttr);
					} else {
						result.addParam(new Param(IS_MFA_ENABLED, FALSE_PARAM, BOOLEAN_PARAM));
						Record hidUsrAttr = new Record();
						hidUsrAttr.setId(USER_ATTRIBUTE_PARAM);
						hidUsrAttr.addStringParam(USER_ID_PARAM, username);
						result.addRecord(hidUsrAttr);
						Record hidSessionAttr = new Record();
						hidSessionAttr.setId("security_attributes");
						hidSessionAttr.addStringParam("session_token", accessToken);
						hidSessionAttr.addStringParam(ACCESS_TOKEN_PARAM, idToken);
						result.addRecord(hidSessionAttr);
						updateUserAttributesFromInfinity(result, request,username);
					}
				} else {
					String errorMsg = result1.getParamValueByName("errormsg");
					errorMsg = StringUtils.isEmpty(errorMsg) ? "Invalid user credentials " : errorMsg;
					LOG.debug("HID::CustomValidateAuthentication ---> Error while Calling Serivce {}.{} with errorMessage {}", serviceName, operationName, errorMsg);
					setErrorToResult(result, errorMsg, -1, 401);
					if (isRMSEnabled) {
						String loginStepResult = AuthenticationConstants.LOGIN_STEP_INVALID;
						LoginRMSApi rmsApi = new LoginRMSApi();
						rmsApi.getTag(hidrms, username, request, loginStepResult, 1);
					}
				}
			} else {
				setErrorToResult(result, "Failed to get Bearer token", -1, 401);
			}
		} catch (Exception e) {
			setErrorToResult(result, e.getMessage(), -2, 500);
			LOG.debug("HID::CustomValidateAuthentication ---> Exception occured while calling CustomValidation Service {}",
					e.getMessage());
			e.printStackTrace();
		}
		return false;
	}



	private String getRandomString(int count) {
		String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int) (Math.random() * (alphaNumericString.length() - 1));
			builder.append(alphaNumericString.charAt(character));
		}
		return builder.toString();
	}

	private void setErrorToResult(Result result, String errmsg, int opstatus, int code) {
		result.addOpstatusParam(opstatus);
		result.addErrMsgParam(errmsg);
		result.addHttpStatusCodeParam(code);
		result.addStringParam("errMsg", errmsg);
	}
	
	private void updateUserAttributesFromInfinity(Result result, DataControllerRequest request,String username) {
		String checkForUserIdentityAttributes = "";
		try {
		   checkForUserIdentityAttributes = GetConfProperties.getProperty(request, AuthenticationConstants.HID_USER_ATR_FLAG);
		} catch (Exception e) {
			LOG.error("HID::CustomValidateAuthentication, Error occurred during fetching property for user attribute, error is : {}", e.getMessage());
		}
		if(!checkForUserIdentityAttributes.isEmpty() && checkForUserIdentityAttributes.equalsIgnoreCase(AuthenticationConstants.USER_ATR_BYPASS)) return;
		CustomerAttributesService caService = new CustomerAttributesService();
		caService.populateUserAttributes(result,username, request);
	}
}
