package com.hid.authentication.postprocessor;

import java.util.List;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class SearchDeviceAuthPostProcessor implements DataPostProcessor2 {
	
	private static final String FRIENDLY_NAME_PARAM = "friendlyName";
    private static final String START_DATE_PARAM = "startDate";
    private static final String EXPIRY_DATE_PARAM = "expiryDate";

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response) //NOSONAR
			throws Exception {
		
		Dataset devices = result.getDatasetById("devices");

		List<Record> records = devices.getAllRecords();	

		for (int i = 0; i < records.size(); i++) {
			if (records.get(i).getParamValueByName(FRIENDLY_NAME_PARAM) != null) {
				
				String friendlyNameValue = records.get(i).getParamValueByName(FRIENDLY_NAME_PARAM);
				String friendlyName = "";
				int indexFriendlyName = friendlyNameValue.indexOf(FRIENDLY_NAME_PARAM);
				if(indexFriendlyName != -1)
				{
					int startIndex = friendlyNameValue.indexOf("=", indexFriendlyName) + 1;
					int endIndex = friendlyNameValue.indexOf(",", startIndex);
					friendlyName = friendlyNameValue.substring(startIndex, endIndex);
				}				
				records.get(i).getParam(FRIENDLY_NAME_PARAM).setValue(friendlyName);
			}
			
			if (records.get(i).getParamValueByName(START_DATE_PARAM) != null) {				
				String startDateValue = records.get(i).getParamValueByName(START_DATE_PARAM);				
				int indexStartDate = startDateValue.indexOf(START_DATE_PARAM);
				String startDate = "";
				String expiryDate = "";
				if(indexStartDate != -1) {					
					String formattedStatus = startDateValue.replace("{", "").replace("}", "").replace(" ", "");
					String[] statusArray = formattedStatus.split(",");
					for(String str : statusArray) {						
						if(str.startsWith(START_DATE_PARAM)){
							startDate = str.substring(10);							
						}
						if(str.startsWith(EXPIRY_DATE_PARAM)) {
							expiryDate = str.substring(11);							
						}
					}
				}
				records.get(i).getParam(START_DATE_PARAM).setValue(startDate);				
				records.get(i).getParam(EXPIRY_DATE_PARAM).setValue(expiryDate);
			}
		}
		
		return result;
	}	
	
	
}
