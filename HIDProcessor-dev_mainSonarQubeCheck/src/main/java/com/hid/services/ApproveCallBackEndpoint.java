package com.hid.services;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.io.BufferedReader;
import java.util.Objects;

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

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
@SuppressWarnings({"java:S1161", "java:S3776", "java:S1141", "java:S1989", "java:S112","java:S5411",
	"java:S2629", "java:S4973", "java:S2696", "java:S2589","java:2776","java:S1066","java:S1874"})
@IntegrationCustomServlet(urlPatterns = "ApproveCallBackEndpoint")
public class ApproveCallBackEndpoint extends HttpServlet {

	private static final long serialVersionUID = 2L;
	private static final Logger LOG = LogManager.getLogger(com.hid.services.ApproveCallBackEndpoint.class);
	private static String port = "";

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			LOG.debug("HID : In ApproveCallBackEndpoint");
			String contentType = req.getContentType();
			LOG.debug("HID : In ApproveCallBackEndpoint contentType : {}", contentType);

			JSONObject requestObj;
			StringBuilder sb = new StringBuilder();
			BufferedReader reader = req.getReader();
			String value;
			while ((value = reader.readLine()) != null) {
				sb.append(value);
			}
			String statusJson = sb.toString();
			LOG.debug("HID : Approve CallBack Endpoint result == {}", statusJson);
			requestObj = new JSONObject(statusJson);

			String authReqId = Objects.toString(requestObj.opt("auth_req_id"), "");
			String accessToken = Objects.toString(requestObj.opt("access_token"));
			String idToken = Objects.toString(requestObj.opt("id_token"));
			String expiresIn = Objects.toString(requestObj.opt("expires_in"));
			LOG.debug("HID : CIBA CallBack - Auth Request Id = {} expires_in = {}, id_token = {}", authReqId, expiresIn, idToken);

			if (idToken != null && !idToken.isEmpty()) {
				JWSObject jwsObject = JWSObject.parse(idToken);
				String jsonIdToken = jwsObject.getPayload().toString();				
				org.json.JSONObject jsonObj = new org.json.JSONObject(jsonIdToken);				

				String clientApprovalStatus = jsonObj.getString("clientapprovalstatus");
				String userId = jsonObj.getString("sub");
				Long idTokenExp =  jsonObj.getLong("exp");

				if (idTokenExp > (System.currentTimeMillis() / 1000)) {
					LOG.debug("HID : Request is not expired");

					String issuer = (String) jsonObj.get("iss");
					String[] domainName = issuer.split("/");

					ServicesManager smgr = null;
					try {
						smgr = ServicesManagerHelper.getServicesManager(req);
					} catch (MiddlewareException e) {
						LOG.error("HID : Error ocurred while creating service manager from http request", e);
						resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
					}

					ConfigurableParametersHelper paramHelper = smgr.getConfigurableParametersHelper();
					String hostName = AuthenticationConstants.HID_HOST_KEY;
					String host = paramHelper.getServerProperty(hostName) == "" ? ""
							: paramHelper.getServerProperty(hostName);

					if (StringUtils.isEmpty(host)) {
						throw new Exception("HID_HOST needs to be configured in server setting");
					}
					String confPort = paramHelper.getServerProperty("HID_CIBA_API_PORT");
					if(!StringUtils.isEmpty(confPort)) {
						port = confPort;
					}
			        String finalHost = host.split(":")[0];
					String actividURI = "https://" + finalHost + "/idp/" + domainName[4];
					LOG.debug("HID : actividURL + domainName: {}", actividURI);
					Boolean isSigned = validateJWT(actividURI, idToken);
					if (isSigned) {
						LOG.debug("HID : CIBA CallBack - Approval Status = {}", String.valueOf(clientApprovalStatus));
						LOG.debug("HID : CIBA CallBack - User Code  = {}", String.valueOf(userId));
						JSONObject statusObj = new JSONObject();
						statusObj.put("clientapprovalstatus", clientApprovalStatus);
						statusObj.put("usercode", userId);
						statusObj.put("access_token", accessToken);
						statusObj.put("expires_in", expiresIn);

						if (StringUtils.isNotBlank(clientApprovalStatus) && StringUtils.isNotBlank(authReqId)) {
							if (smgr != null)
								smgr.getResultCache().insertIntoCache(authReqId, statusObj.toString());
						}
						resp.setStatus(HttpServletResponse.SC_OK);
					} else {
						LOG.error("HID : Signature is not valid");
						resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid signature");
					}
				} else {
					LOG.error("HID : Request is expired");
					resp.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT, "Request got expired");
				}
			}
		} catch (Exception e) {
			LOG.error("HID : Exception occurred during processing Approve callback", e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}

	private Boolean validateJWT(String actividURI, String idToken) throws Exception {

		// Parse the id_token and verify its RSA signature
		SignedJWT signedJWT = SignedJWT.parse(idToken);
		String issuer = signedJWT.getJWTClaimsSet().getIssuer();

		if (issuer.equalsIgnoreCase(actividURI + "/authn")) {
			LOG.debug("HID : Issuer isValid: {}", issuer.equalsIgnoreCase(actividURI + "/authn"));
			String finalUrl = StringUtils.isEmpty(port) ? actividURI : addPort(actividURI, port);
			LOG.debug("HID : Final URI For OpenId Config is : {}", finalUrl);
			
			OIDCProviderMetadata idpDiscovery = getPayload(finalUrl + "/authn/.well-known/openid-configuration");
			
			String jwksURL = idpDiscovery.getJWKSetURI().toURL().toString();
			String finalJWKSUrl =  StringUtils.isEmpty(port) ?  jwksURL : addPort(jwksURL,port);
			LOG.debug("HID : Final URI For OpenId JWKS is : {}", finalJWKSUrl);
			
			JWKSet publicKeys = JWKSet.load(new URL(finalJWKSUrl));

			// Parse key Id used to signed JWT
			String keyId = signedJWT.getHeader().getKeyID();

			List<JWK> matches = new JWKSelector(new JWKMatcher.Builder().keyType(KeyType.RSA).keyID(keyId).build())
					.select(publicKeys);

			if (matches.size() != 1) {
				LOG.error("HID : Could not find public signing key to verify signed JWT");
				return false;
			}

			// Get the only key
			RSAKey spkey = (RSAKey) matches.get(0);

			// Set the key on the verifier
			JWSVerifier verifier = new RSASSAVerifier(spkey.toRSAPublicKey());

			if (signedJWT.verify(verifier)) {
				LOG.debug("HID : JWT signature is valid: {}", signedJWT.verify(verifier));
				return true;
			} else {
				LOG.error("HID : JWT signature verification failed");
				return false;
			}
		}

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

}
