package com.hid.usermanagement.preprocessor;

import java.util.HashMap;

import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;
import org.json.JSONArray;

import com.hid.common.ClientBasePreprocessor;
import com.hid.util.HIDFabricConstants;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
@SuppressWarnings({"java:S3776", "java:S1125", "java:S5411", "java:S2589", "java:S3457", "java:S2629", "java:S4973"})
public class UpdateUserMobilePreProcessor extends ClientBasePreprocessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager.getLogger(
			com.hid.usermanagement.preprocessor.UpdateUserMobilePreProcessor.class);
	private static final String USER_ID = "userId";
	private static final String SEQUENCE_FAILED = "sequenceFailed";
	private static final String ERROR_MSG_DETAIL = "errorMsgDetail";

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response, Result result)
			throws Exception {

		if (super.execute(inputMap, request, response, result)) {
			LOG.debug("HID : In UpdateUserMobilePreProcessor");
			Boolean userExists = (request.getAttribute("userExists") == null) ? true : request.getAttribute("userExists");
			LOG.debug("HID : values of userExists = {}", userExists);
			String userId = request.getAttribute(USER_ID) == null ? "" : request.getAttribute(USER_ID);
			LOG.debug("HID : values of userId = {}", userId);
			if (!userExists || userId.isEmpty()) {
				LOG.debug(
						"HID : Either user doesn't exist or userid is empty, setting User Not Exists error message");
				result.addOpstatusParam(-1);
				result.addErrMsgParam(HIDFabricConstants.USER_NOT_EXIST);
				request.setAttribute(SEQUENCE_FAILED, true);
				request.setAttribute(ERROR_MSG_DETAIL,
						HIDFabricConstants.USER_NOT_EXIST);
				return false;
			}

			Dataset userAttributes = request.getAttribute("attributes");
			LOG.debug("HID : values of userAttributes = {}", userAttributes.getAllRecords());
			String mobileNumber = inputMap.get("mobileNumber") == null ? "" : (String) inputMap.get("mobileNumber");
			LOG.debug("HID : value of mobileNumber = {}", mobileNumber);
			if (mobileNumber == "" || mobileNumber.isEmpty() || mobileNumber == null) {
				result.addOpstatusParam(-1);
				result.addErrMsgParam(HIDFabricConstants.MOMBILENO_NOT_EXIST);
				request.setAttribute(SEQUENCE_FAILED, true);
				request.setAttribute(ERROR_MSG_DETAIL,
						HIDFabricConstants.MOMBILENO_NOT_EXIST);
				return false;
			}
			//Update Mobile Number for the User
			for (Record r : userAttributes.getAllRecords()) {
				if ("ATR_MOBILE".equals(r.getParamValueByName("name"))) {
					r.getParam("value").setValue(mobileNumber);
					break;
				}
			}
			try {
				JSONArray attributesArray = ResultToJSON .convertDataset(userAttributes);
				LOG.debug("HID : UpdateUserPreProcessor, attributes {} userId {}", attributesArray.toString(), userId);
				inputMap.put("attributes", attributesArray.toString());
				
			}catch (Exception e) {
				LOG.debug(
						"HID : Error while parsing the attributes");
				result.addOpstatusParam(-1);
				result.addErrMsgParam("Error while parsing the attributes");
				request.setAttribute(SEQUENCE_FAILED, true);
				request.setAttribute(ERROR_MSG_DETAIL,HIDFabricConstants.ATTRIBUTE_PARSING_ERROR);
				return false;
			}
			inputMap.put(USER_ID, userId);
			return true;
		}
		return false;
	}
}
