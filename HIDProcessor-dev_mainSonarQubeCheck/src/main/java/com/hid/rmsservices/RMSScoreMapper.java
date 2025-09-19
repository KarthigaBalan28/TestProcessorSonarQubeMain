package com.hid.rmsservices;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.hid.util.AuthenticationConstants;

public class RMSScoreMapper {
    private String scoreMapString;
    private boolean isCustomMap;
    public RMSScoreMapper(boolean isCustomMap, String scoreMapString) {
    	this.isCustomMap = isCustomMap;
    	this.scoreMapString = scoreMapString;
    }
    
    public Integer getMappedScore(Integer score) {
    	if(isCustomMap && !parseCustomMapString()) {
    		scoreMapString = AuthenticationConstants.RMS_DEFAULT_SCORE_MAP;
    	}
    	try {
    		JSONObject scoreMapObj= new JSONObject(scoreMapString);
    		for(int i=0;i<=10;i++) {
    			JSONObject rangeObj = scoreMapObj.getJSONObject(""+i);
    			Integer lowerLimit = Integer.parseInt(rangeObj.optString("lowerLimit"));
    			Integer upperLimit = Integer.parseInt(rangeObj.optString("upperLimit"));
    			if(isInRange(score, lowerLimit, upperLimit)) {
    				return i;
    			}
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    		return -3;
    	}
    	return -1;
    }
    
    private boolean parseCustomMapString() {
    	try {
    		JSONObject scoreMapObj= new JSONObject(scoreMapString);
    		for(int i=0; i<=10;i++) {
    			JSONObject rangeObj = scoreMapObj.getJSONObject(""+i);
    			if(rangeObj == null) {
    				return false;
    			}
    			String lowerLimit = rangeObj.getString("lowerLimit");
    			String upperLimit = rangeObj.getString("upperLimit");
    			if(StringUtils.isEmpty(lowerLimit) || StringUtils.isEmpty(upperLimit)){
    				return false;
    			}
    			if(!StringUtils.isNumeric(lowerLimit) || !StringUtils.isNumeric(upperLimit)) {
    				return false;
    			}
    		}
    	}catch(Exception e)	{
    		e.printStackTrace();
    		return false;
    	}	
    	return true;
    }
    
    ///Range is caluculated as closed Interval Lower Limit, Open Interval UpperLimit ex: [100,300)
    private boolean isInRange(int key , int lower,int upper) {
    	return key >= lower && key < upper;
    }
}
