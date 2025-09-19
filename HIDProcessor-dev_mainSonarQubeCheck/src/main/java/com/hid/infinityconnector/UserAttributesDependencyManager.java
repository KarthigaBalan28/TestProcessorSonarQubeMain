package com.hid.infinityconnector;
@SuppressWarnings("java:S1118")
public class UserAttributesDependencyManager {
    public static UserAttributesService getAttributesConnector() {
    	 return new DpbProductIdentityConnecter();
    }
}
