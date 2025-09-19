package com.hid.dataclasses;

@SuppressWarnings("java:S107")
public class HIDRMSDataclass {

	private String applicationId;
	
	private String channelID;
	
	//cookies
	private String tmDeviceTag;
	
	//cookies
	private String tmSessionId;
	
	//clinetIp
	private String clientIp;
	
	private String appuserId;
	
	private String appActionId;
	
	private String loginStepResult;
	
	private int loginFactorIndex;
	
	private String securityItemType;
	
	private String appSessionId;
	
	private String securityItemId;
	
	private String environmentId;
	
	
	
	public HIDRMSDataclass(String applicationId, String channelID,String environmentID, String tmDeviceTag, String tmSessionId,
			String clientIp, int loginFactorIndex, String securityItemType, String appSessionId, String securityItemId) {
		super();
		this.applicationId = applicationId;
		this.channelID = channelID;
		this.tmDeviceTag = tmDeviceTag;
		this.tmSessionId = tmSessionId;
		this.clientIp = clientIp;
		this.loginFactorIndex = loginFactorIndex;
		this.securityItemType = securityItemType;
		this.appSessionId = appSessionId;
		this.securityItemId = securityItemId;
		this.environmentId = environmentID;
	}



	public HIDRMSDataclass(String applicationId, String channelID, String environmentID,String tmDeviceTag, String tmSessionId,
			String appSessionId, String clientIp, String securityItemType, String securityItemId) {
		super();
		this.applicationId = applicationId;
		this.channelID = channelID;
		this.tmDeviceTag = tmDeviceTag;
		this.tmSessionId = tmSessionId;
		this.appSessionId = appSessionId;
		this.clientIp = clientIp;
		this.securityItemType = securityItemType;
		this.securityItemId = securityItemId;
		this.environmentId = environmentID;
	}

	
	
	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getChannelID() {
		return channelID;
	}
	
	public String getEnvironmentID() {
		return environmentId;
	}

	public void setChannelID(String channelID) {
		this.channelID = channelID;
	}

	public String getTmDeviceTag() {
		return tmDeviceTag;
	}

	public void setTmDeviceTag(String tmDeviceTag) {
		this.tmDeviceTag = tmDeviceTag;
	}

	public String getTmSessionId() {
		return tmSessionId;
	}

	public void setTmSessionId(String tmSessionId) {
		this.tmSessionId = tmSessionId;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	
	

	public String getAppuserId() {
		return appuserId;
	}

	public void setAppuserId(String appuserId) {
		this.appuserId = appuserId;
	}

	public String getAppActionId() {
		return appActionId;
	}

	public void setAppActionId(String appActionId) {
		this.appActionId = appActionId;
	}

	public String getLoginStepResult() {
		return loginStepResult;
	}

	public void setLoginStepResult(String loginStepResult) {
		this.loginStepResult = loginStepResult;
	}

	public int getLoginFactorIndex() {
		return loginFactorIndex;
	}

	public void setLoginFactorIndex(int loginFactorIndex) {
		this.loginFactorIndex = loginFactorIndex;
	}

	public String getSecurityItemType() {
		return securityItemType;
	}

	public void setSecurityItemType(String securityItemType) {
		this.securityItemType = securityItemType;
	}

	public String getAppSessionId() {
		return appSessionId;
	}

	public void setAppSessionId(String appSessionId) {
		this.appSessionId = appSessionId;
	}

	public HIDRMSDataclass(String applicationId, String channelID, String tmDeviceTag, String tmSessionId,
			String clientIp) {
		super();
		this.applicationId = applicationId;
		this.channelID = channelID;
		this.tmDeviceTag = tmDeviceTag;
		this.tmSessionId = tmSessionId;
		this.clientIp = clientIp;
	}

	public HIDRMSDataclass() {
	}



	public String getSecurityItemId() {
		return securityItemId;
	}

	public void setSecurityItemId(String securityItemId) {
		this.securityItemId = securityItemId;
	}


	
}
