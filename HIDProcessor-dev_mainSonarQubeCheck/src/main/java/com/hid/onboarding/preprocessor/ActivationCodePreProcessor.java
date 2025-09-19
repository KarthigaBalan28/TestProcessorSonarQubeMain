package com.hid.onboarding.preprocessor;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.HIDFabricConstants;
import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ActivationCodePreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {
	
  private static final Logger LOG = LogManager.getLogger(ActivationCodePreProcessor.class);
  
  @SuppressWarnings({"java:S3776","java:S1185"})
  @Override
  public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response, Result result) throws Exception {
    if (super.execute(inputMap, request, response, result)) {
      LocalDateTime now;
      LOG.debug("HID : In ActivationCodePreProcessor");
      String startDateFromReq = Objects.toString(inputMap.get("startDate"), "");
      if (!startDateFromReq.isEmpty() && StringUtils.isNumeric(startDateFromReq)) {
        long epochTime = Long.parseLong(startDateFromReq);
        now = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochTime), ZoneId.systemDefault());
      } else {
        now = LocalDateTime.now();
      } 
      String start = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(now);
      String expiryTimeDuration = GetConfProperties.getProperty(request, HIDFabricConstants.ACT_EXPIRY_TIME);
      long daysToExpire = HIDFabricConstants.DEFAULT_ACT_EXPIRY_DAYS.longValue();
      if (!expiryTimeDuration.isEmpty() && StringUtils.isNumeric(expiryTimeDuration))
        daysToExpire = Long.parseLong(expiryTimeDuration); 
      LocalDateTime next = now.plusDays(daysToExpire);
      String end = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(next);
      String offSetTime = GetConfProperties.getProperty(request, "HID_OFFSET_TIME");
      String startDate = "";
      String expDate = "";
      if (!offSetTime.isEmpty()) {
        startDate = ((start.indexOf('.') == -1) ? start : start.substring(0, start.indexOf('.'))) + offSetTime;
        expDate = ((end.indexOf('.') == -1) ? end : end.substring(0, end.indexOf('.'))) + offSetTime;
      } else {
        startDate = ((start.indexOf('.') == -1) ? start : start.substring(0, start.indexOf('.'))) 
        		+ HIDFabricConstants.OFFSET_TIME;
        expDate = ((end.indexOf('.') == -1) ? end : end.substring(0, end.indexOf('.'))) 
        		+ HIDFabricConstants.OFFSET_TIME;
      } 
      String authType = GetConfProperties.getProperty(request, AuthenticationConstants.HID_ACTIVATION_CODE_AUTHTYPE);
      if (!authType.isEmpty()) {
        LOG.debug("HID : Setting AuthType to {} in input parameter from server settings", authType);
        inputMap.put("authType", authType);
      } 
      inputMap.put("startDate", startDate);
      inputMap.put("expDate", expDate);
      return true;
    } 
    return false;
  }
}
