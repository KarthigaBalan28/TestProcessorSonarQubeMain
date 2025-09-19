package com.hid.identity.util;
import org.apache.logging.log4j.Logger; 
import org.apache.logging.log4j.LogManager;

@SuppressWarnings({"java:S1118" , "java:S3416" , "java:S2629"})
public class IdentityLogger {
	 private static final Logger LOG = LogManager.getLogger(com.hid.authentication.preprocessor.CustomMFAValidation.class);
     public static void debug(String module, String className, String str) {
    	LOG.debug(String.format("HIDIdentity ---> Module : %s, class : %s , Msg : %s", module, className, str));
     }     
}
