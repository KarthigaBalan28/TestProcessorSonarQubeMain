package com.hid.rmsservices;

import org.apache.commons.lang3.StringUtils;

import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class RMSBasePostprocessor implements DataPostProcessor2 {

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		String isCustomMapFromProp = GetConfProperties.getProperty(request, "IS_CUSTOM_RMS_SCORE_RANGE");
		boolean isCustomMap = isCustomMapFromProp.equalsIgnoreCase("true");
		String scoreMapString = AuthenticationConstants.RMS_DEFAULT_SCORE_MAP;
		if(isCustomMap) {
			String customMapString = GetConfProperties.getProperty(request, "CUSTOM_RMS_SCORE_MAP");
			scoreMapString = "".equals(customMapString) ? scoreMapString : customMapString;
		}
		String riskScore = result.getParamValueByName("risk");
		String appActionId = request.getAttribute("appActionId");
		if(!StringUtils.isEmpty(appActionId)) {
			result.addStringParam("app_action_id", appActionId);
		}
		RMSScoreMapper mapper = new RMSScoreMapper(isCustomMap, scoreMapString);
		Integer infinityScore =  riskScore!=null ?   mapper.getMappedScore(Integer.parseInt(riskScore)) : -2;
		result.addIntParam("currentThreat", infinityScore);
		return result;
	}

}
