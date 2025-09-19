package com.hid.services;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger; import org.apache.logging.log4j.LogManager;

import com.hid.util.AuthenticationConstants;
import com.konylabs.middleware.api.ConfigurableParametersHelper;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.api.ServicesManagerHelper;
import com.konylabs.middleware.exceptions.MiddlewareException;
import com.konylabs.middleware.servlet.IntegrationCustomServlet;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPRequest.Method;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.http.ServletUtils;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import net.minidev.json.JSONObject;
@SuppressWarnings({"java:S1161","java:S2696","java:S3776","java:S1141", "java:S1989", "java:S112","java:S1125","java:S1874"})
@IntegrationCustomServlet(urlPatterns = "CIBATestEndpoint")
public class CIBATestEndpoint extends HttpServlet {

	private static final long serialVersionUID = 2L;
	private static final Logger LOG = LogManager.getLogger(CIBATestEndpoint.class);
	private static String port = "";
	private static String msg = "";

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			LOG.debug("HID : In ApproveCallBackEndpoint");
			HTTPRequest httpRequest = ServletUtils.createHTTPRequest(req);
            msg += "CIBA_Callback_Reached : success |";
			JSONObject requestObj = httpRequest.getQueryAsJSONObject();

			LOG.debug("HID : CIBA Approval callback response == {}", requestObj);

			String authReqId = requestObj.getAsString("auth_req_id");
			String accessToken = requestObj.getAsString("access_token");
			String idToken = requestObj.getAsString("id_token");
			String expiresIn = requestObj.getAsString("expires_in");
			String isSignValidationRequired = requestObj.getAsString("isSignValidationRequired");
			
			LOG.debug("HID : CIBA CallBack - Auth Request Id = {}, expires_in = {}, id_token = {}", authReqId, expiresIn, idToken);

			if (idToken != null && !idToken.isEmpty()) {
				msg += " Id_token Check : Success |";
				JWSObject jwsObject = JWSObject.parse(idToken);
				String jsonIdToken = jwsObject.getPayload().toString();				
				org.json.JSONObject jsonObj = new org.json.JSONObject(jsonIdToken);				

				String clientApprovalStatus = jsonObj.getString("clientapprovalstatus");
				String userId = jsonObj.getString("sub");
				Long idTokenExp =  jsonObj.getLong("exp");

				if (idTokenExp > (System.currentTimeMillis() / 1000)) {
					LOG.debug("HID : Request is not expired");
					msg += " Token Expiry : Active |";
					String issuer = (String) jsonObj.get("iss");
					String[] domainName = issuer.split("/");

					ServicesManager smgr = null;
					try {
						smgr = ServicesManagerHelper.getServicesManager(req);
					} catch (MiddlewareException e) {
						LOG.error("HID : Error ocurred while creating service manager from http request", e);
						msg += " Fabric Error while getting servicManager";
						setResponseMsg(resp,msg);
						resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
					}

					ConfigurableParametersHelper paramHelper = smgr.getConfigurableParametersHelper();
					String hostName = AuthenticationConstants.HID_HOST_KEY;
					String host = paramHelper.getServerProperty(hostName) == "" ? ""
							: paramHelper.getServerProperty(hostName);

					if (StringUtils.isEmpty(host)) {
						msg += " Config Error : HID_HOST needs to be configured in server setting";
						setResponseMsg(resp,msg);
						throw new Exception("HID_HOST needs to be configured in server setting");
					}
					String confPort = paramHelper.getServerProperty("HID_CIBA_API_PORT");
					if(!StringUtils.isEmpty(confPort)) {
						port = confPort;
					}
			        String finalHost = host.split(":")[0];
					String actividURI = "https://" + finalHost + "/idp/" + domainName[4];
					LOG.debug("HID : actividURL + domainName: {}" , actividURI);
					boolean isSigned = isSignValidationRequired ==null || isSignValidationRequired.equals("false") ? true : validateJWT(actividURI, idToken);
					if (isSigned) {
						msg += " Signing Status : Valid";
						LOG.debug("HID : CIBA CallBack - Approval Status = {}", clientApprovalStatus);
						LOG.debug("HID : CIBA CallBack - User Code  = {}", userId);
						JSONObject statusObj = new JSONObject();
						statusObj.put("clientapprovalstatus", clientApprovalStatus);
						statusObj.put("usercode", userId);
						statusObj.put("access_token", accessToken);
						statusObj.put("expires_in", expiresIn);

						if (StringUtils.isNotBlank(clientApprovalStatus) && StringUtils.isNotBlank(authReqId) && smgr != null) {
							
								smgr.getResultCache().insertIntoCache(authReqId, statusObj.toString());
						}
						setResponseMsg(resp, msg);
						resp.setStatus(HttpServletResponse.SC_OK);
					} else {
						msg += " Server Error : Id_Token Signing Failed";
						setResponseMsg(resp,msg);
						LOG.error("HID : Signature is not valid");
						resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					}
				} else {
					LOG.error("HID : Request is expired");
					msg += " Server Error : Request is expired";
				    setResponseMsg(resp,msg);
				    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			}
		} catch (Exception e) {
			LOG.error("HID : Exception occurred during processing Approve callback, error message : {}", e.getMessage(), e);
			msg += " Server Error : " + e.getLocalizedMessage();
		    setResponseMsg(resp,msg);
		    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

	}

	private boolean validateJWT(String actividURI, String idToken) throws Exception {

		// Parse the id_token and verify its RSA signature
		SignedJWT signedJWT = SignedJWT.parse(idToken);
		String issuer = signedJWT.getJWTClaimsSet().getIssuer();

		if (issuer.equalsIgnoreCase(actividURI + "/authn")) {
			msg += " IssuerValidation : success |";
			LOG.debug("HID : Issuer isValid: {}", issuer.equalsIgnoreCase(actividURI + "/authn"));
			String finalUrl = StringUtils.isEmpty(port) ? actividURI : addPort(actividURI, port);
			LOG.debug("HID : Final URI For OpenId Config is : {}",  finalUrl);
			
			OIDCProviderMetadata idpDiscovery = getPayload(finalUrl + "/authn/.well-known/openid-configuration");
			msg += " Well Known OpenID URL : success |";
			String jwksURL = idpDiscovery.getJWKSetURI().toURL().toString();
			String finalJwksUrl =  StringUtils.isEmpty(port) ?  jwksURL : addPort(jwksURL,port);
			LOG.debug("HID : Final URI For OpenId JWKS is : {}", finalJwksUrl);
			
			JWKSet publicKeys = JWKSet.load(new URL(finalJwksUrl));
			msg += " JWKS Public Keys : success |";
			// Parse key Id used to signed JWT
			String keyId = signedJWT.getHeader().getKeyID();

			List<JWK> matches = new JWKSelector(new JWKMatcher.Builder().keyType(KeyType.RSA).keyID(keyId).build())
					.select(publicKeys);

			if (matches.size() != 1) {
				msg += " Key Validation : failed |";
				LOG.error("HID : Could not find public signing key to verify signed JWT");
				return false;
			}

			// Get the only key
			RSAKey spkey = (RSAKey) matches.get(0);

			// Set the key on the verifier
			JWSVerifier verifier = new RSASSAVerifier(spkey.toRSAPublicKey());

			if (signedJWT.verify(verifier)) {
				msg += " Signature Validation : Success |";
				LOG.debug("HID : JWT signature is valid: {}", signedJWT.verify(verifier));
				return true;
			} else {
				msg += " Signature Validation : Failed |";
				LOG.error("HID : JWT signature verification failed");
				return false;
			}
		}
		msg += " IssuerValidation : failed |";
		LOG.error("HID : Failed to verify issuer of the id_token: {}", issuer);
		return false;
	}

	public static OIDCProviderMetadata getPayload(String openidConfigurationURL) throws Exception {
		HTTPRequest configRequest = new HTTPRequest(Method.GET, new URL(openidConfigurationURL));
		HTTPResponse configResponse = configRequest.send();
		return OIDCProviderMetadata.parse(configResponse.getContent());
	}
	
	private static String addPort(String url, String port){
		String[] arr = url.split("/");
		arr[2] += ":";
		arr[2] += port;
		return String.join("/", arr);
    }
	
	private static void setResponseMsg(HttpServletResponse response, String msg) {
		  JSONObject jsonObject = new JSONObject();
	      jsonObject.put("CibaListenerMsg", msg);
	      response.setContentType("application/json");
	      response.setCharacterEncoding("UTF-8");
	      try {
			response.getWriter().write(jsonObject.toString());
		  } catch (IOException e) {
			e.printStackTrace();
		  }
	}

}
