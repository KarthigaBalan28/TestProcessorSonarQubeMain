package com.hid.identity.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hid.util.AuthenticationConstants;
import com.hid.util.GetConfProperties;
import com.hid.util.HIDIntegrationServices;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import java.text.ParseException;
import java.net.URL;

@SuppressWarnings({"java:S1118" , "java:S1172"})
public class JWTValidationManager {

    private static final Logger LOG = LogManager.getLogger(JWTValidationManager.class);

    private static JWKSet cachedJWKSet = null;
    private static long jwkSetExpiryTime = 0;
    private static final long JWK_CACHE_TTL_MS = 10 * 60 * 1000L; // 10 mins
    private static String cachedIssuer = "";

    public static boolean verifyToken(String idToken, DataControllerRequest request, DataControllerResponse response,
                                      Result result) throws com.nimbusds.oauth2.sdk.ParseException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            String issuerFromToken = signedJWT.getJWTClaimsSet().getIssuer();

            String host = Objects.toString(
                    GetConfProperties.getProperty(request, AuthenticationConstants.HID_HOST_KEY), "");
            String tenant = Objects.toString(
                    GetConfProperties.getProperty(request, AuthenticationConstants.HID_TENANT_KEY), "");
            String authURI = "https://" + host + "/idp/" + tenant + "/authn";
            LOG.debug("HID --> JWTValidationManager -> authURI: {}", authURI);
            if (!issuerFromToken.trim().equals(authURI.trim())) {
                LOG.error("HID --> JWTValidationManager -> Issuer mismatch");
                return false;
            }

            // Refresh JWKS only if expired or issuer not cached
            if (cachedJWKSet == null || System.currentTimeMillis() > jwkSetExpiryTime || !authURI.equals(cachedIssuer)) {
                synchronized (JWTValidationManager.class) {
                    if (cachedJWKSet == null || System.currentTimeMillis() > jwkSetExpiryTime || !authURI.equals(cachedIssuer)) {
                        LOG.debug("HID --> JWTValidationManager -> JWKS cache expired. Refreshing...");

                        HashMap<String, Object> hMap = new HashMap<>();
                        Result jwtResult = HIDIntegrationServices.call("HIDOpenIDConfigService", "fetchOpenIDConfig", request, hMap, hMap);
                        String jwtResponse = ResultToJSON.convert(jwtResult);
                        OIDCProviderMetadata idpMetadata = OIDCProviderMetadata.parse(jwtResponse);

                        URL jwksURL = idpMetadata.getJWKSetURI().toURL();
                        cachedJWKSet = JWKSet.load(jwksURL);
                        jwkSetExpiryTime = System.currentTimeMillis() + JWK_CACHE_TTL_MS;
                        cachedIssuer = authURI;
                        LOG.debug("HID --> JWTValidationManager -> JWKS and issuer cached successfully.");
                    }
                }
            }
            String keyId = signedJWT.getHeader().getKeyID();
            List<JWK> matches = new JWKSelector(
                    new JWKMatcher.Builder().keyType(KeyType.RSA).keyID(keyId).build()
            ).select(cachedJWKSet);

            if (matches.size() != 1) {
                LOG.error("HID --> JWTValidationManager -> No matching key found in JWKS");
                return false;
            }

            RSAKey rsaKey = (RSAKey) matches.get(0);
            JWSVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());

            if (signedJWT.verify(verifier)) {
                LOG.debug("HID --> JWTValidationManager -> Token signature verified");
                return true;
            } else {
                LOG.error("HID --> JWTValidationManager -> Token signature verification failed");
                return false;
            }

        } catch (Exception e) {
            LOG.error("HID --> JWTValidationManager -> Exception in verifyToken()", e);
            return false;
        }
    }

    public static boolean validateTokenExpiry(String idToken) throws ParseException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            if (claimsSet == null) return false;

            Date now = new Date();
            Date expiration = claimsSet.getExpirationTime();

            return expiration != null && now.before(expiration);
        } catch (java.text.ParseException e) {
            LOG.error("HID --> JWTValidator -> Error parsing JWT for expiry", e);
            return false;
        }
    }
}
