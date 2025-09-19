package com.hid.customotp;
@SuppressWarnings("java:S1118")
public class DependencyManager {

	public static SMSServiceMetaData getServiceMetaData() {
	    return new CustomSendOTPServiceMeta();
	}

	public static SMSCarrierService getCarrierService() {
		return new HIDCustomCarrierService();
	}

}
