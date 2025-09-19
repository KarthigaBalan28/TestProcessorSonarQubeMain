package com.hid.idv.service;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hid.idv.utils.IDVConstants;
import com.hid.util.GetConfProperties;
import com.hid.util.HIDIntegrationServices;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

@SuppressWarnings({"java:S1854", "java:S2629", "java:S1192", "java:S1172", "java:S2293", "java:S3740"})
public class GetVerifiedImages implements JavaService2 {
	private static final Logger LOG = LogManager
			.getLogger(com.hid.idv.service.GetVerifiedImages.class);
	@Override
	public Result invoke(String methodID, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
		LOG.debug("HID-IDV GetVerifiedImages Java service");
		Result imgResult = new Result();
		Record imgRecord = new Record();
		Result tempResult = new Result();
		Dataset imgDataset = new Dataset();
		String imagesNames = GetConfProperties.getProperty(request, IDVConstants.HID_IDV_IMAGES_NAMES_LIST_KEY);
		LOG.debug("HID-IDV :{}", imagesNames);
		imagesNames = imagesNames.replaceAll("[{}\"]", "");
		String[] imagesList = imagesNames.split(",");
		HashMap<String, Object> inputMap = (HashMap<String, Object>) inputArray[1];	     
        LOG.debug("HID-IDV : imagesList.length: {}", imagesList.length);
        for (String imageName : imagesList) {
            tempResult = getDocumentImages(inputMap, request, imageName);
            LOG.debug("HID-IDV GetVerifiedImages tempResult status:{} ", tempResult.getParamValueByName("Status"));
            if(tempResult.getParamValueByName("Status") != null && tempResult.getParamValueByName("Status").equals("Success")) {            	
            	imgRecord = tempResult.getRecordById("DocumentImage");
            	imgRecord.addParam("ImageName", imgRecord.getParamValueByName("ImageName"));    
            	imgRecord.addParam("Base64Image", imgRecord.getParamValueByName("Base64Image"));  
            } else { 
            	LOG.debug("HID-IDV GetVerifiedImages else part tempResult status:{} ", tempResult.getParamValueByName("Status"));
            	imgRecord = tempResult.getRecordById("DocumentImage");
            	imgRecord.addParam("ImageName", imageName);    
            	imgRecord.addParam("Base64Image", "");    
            	imgRecord.addParam("Details", (imageName + "image is not available" ));            	
            }  
            imgDataset.addRecord(imgRecord);  
        }
        imgDataset.setId("DocumentImage");
        imgResult.addDataset(imgDataset);
        return imgResult;
	}
	
	private Result getDocumentImages (HashMap inputMap, DataControllerRequest request, String imageName) throws Exception {
		Result result = new Result();
		LOG.debug("HID-IDV GetVerifiedImages getDocumentImages");
		result = HIDIntegrationServices.call("GetVerifiedImages", "getVerifiedImages", request, getHeadersMap(inputMap,request), getBodyMap(inputMap,request, imageName));
	    return result;		
	}
	
	private HashMap<String, Object> getBodyMap(HashMap inputMap, DataControllerRequest request, String imageName) {
		HashMap<String, Object> bodyMap = new HashMap<String, Object>();
		bodyMap.put("imageName", imageName);
		bodyMap.put("transactionId", inputMap.get("transactionId"));
		return bodyMap;
	}

	private HashMap<String, Object> getHeadersMap(HashMap inputMap, DataControllerRequest request) throws Exception {
		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		String accountAccessKey = GetConfProperties.getProperty(request, IDVConstants.IDV_ACCOUNT_ACCESS_KEY);
		String secretToken = GetConfProperties.getProperty(request, IDVConstants.IDV_SECRET_TOKEN_KEY);
		headerMap.put(IDVConstants.ACCOUNT_KEY_PARAM, accountAccessKey);
		headerMap.put(IDVConstants.SECRET_TOKEN_PARAM, secretToken);
		return headerMap;
	}
}


