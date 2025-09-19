package com.hid.services;

import java.net.URL;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

@SuppressWarnings({"java:S1854","java:S112","java:S1874", "java:S1481"})
public class JWTester {
	private static final Logger LOG = LogManager.getLogger(com.hid.services.JWTester.class);
	private static Boolean validateJWT(String actividURI, String idToken) throws Exception {

		// Parse the id_token and verify its RSA signature
		SignedJWT signedJWT = SignedJWT.parse(idToken);
		String issuer = signedJWT.getJWTClaimsSet().getIssuer();

		if (issuer.equalsIgnoreCase(actividURI + "/authn")) {
		
			LOG.info("HID : Issuer isValid: {}", issuer.equalsIgnoreCase(actividURI + "/authn"));
			OIDCProviderMetadata idpDiscovery = getPayload(actividURI + "/authn/.well-known/openid-configuration");

			// Load JWK set from IDP endpoint from OIDC discovery configuration
			JWKSet publicKeys = JWKSet.load(idpDiscovery.getJWKSetURI().toURL());

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
				LOG.info("HID : JWT signature is valid: {}", signedJWT.verify(verifier));
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
	
	public static void main(String ...args){
		String actividURI =  "https://aprnp.investbank.ae/idp/IBIDPNPSEC";
		String idToken = "eyJraWQiOiIxNjg4NjM4NTQ5ODc1IiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJhdF9oYXNoIjoidWpCZ1h0S3lndFJKWFBTS01OQzYydyIsInN1YiI6IjkwNjA2MzAyNzEiLCJyZWFzb24iOiJSZWFzb24gbm90IGRlZmluZWQiLCJKV1MiOiJleUpqZEhraU9pSjBaWGgwWEM5d2JHRnBiaUlzSW1Gc1p5STZJbEJUTlRFeUluMC5leUowWkhNaU9pSklaV3hzYnlBNU1EWXdOak13TWpjeFhISmNibEJzWldGelpTQjJZV3hwWkdGMFpTQnNiMmR2YmlJc0ltTnNhV1Z1ZEdGd2NISnZkbUZzYzNSaGRIVnpJam9pWVdOalpYQjBJaXdpZEhoamIzVnVkR1Z5SWpvaU55SjkubHN6WjFoTjVRVHpJVXl2R2FQVUV4aUJ5WUxtOGRIekpaUmNXNzFacmxGLVpBQjBsaTZKemtiSWZ6TlVvRG9DelhwMFh2WndqUWRaMmc2cjl4bG15Tko4WUFWUmJVSEhyS3l0bkNMMTNVcU9ZM1l1U0JSbUNjbkI5WGlTMGxURmo3OUVWLU01cU1KY21HNTI4N1gwb1dsc3FIRUtEazRPdnJEeHFKWXlib1pnUkYtMEk0NTY2NU5DdzAtNHA5cUpDV3hEeklnZG81d3g5ZVBRcDk5SG1nZkJubDBaenlUcl9aODlJRzZ2Y0JROW1lZVg1a0xESTFNNmxvSUhuMTFfNk5RM0piTUhuYnlLNjNyUXlZajBUQ2JLN1IwYlhuN0k1SjdGWUZRNTIzNDBIWnNGVkZ2MGlUcjNSaGx5VGZZaUdYejJIWFhDdDhhbHExYmJoOGl2cVVnIiwiUFVLIjoiTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUEyb1NEc2FmUzl6dmEzeGVSYUhJOEN0SHFVcHpSaHp5dXM3dzRqdy9YOTVMQkZuUUljV1RUWmtsV3NjNWhpekJscGRHUXBQK2UvUElBWVNnOVpyZm1TanFxeThmc2NWbk11bEVKSDFJSzUxbmN2SktIZ3k4bGM1c3Y5Zms2Q2I1WEZCbFJ0enhzNlUzZlJ6MGpkclJBZEhaS0VSM3BPdTNvZHoxOTV4UnpiM0VUWnVsa1czY1VLQktUWEwwczJuU0huNnFmZkthNDlaU1pYSnl0b2xxa1FsOWxuR2ViWEorUWthUXVvbkJYbFBWeXBnT3NXK0Nuc0xpRGRGRzhxd2tQdldzdlo0R1h6aGIxVGpHWm1sS2xjZXc3NjRDRnNMN1plRGpSWFNpSnFEN0MrV2tvSlRLNFV0ZlFibFRSa3JVUHgrelZFRVMycGFuTXlSMnNkWEVrendJREFRQUIiLCJpc3MiOiJodHRwczovL2Fwcm5wLmludmVzdGJhbmsuYWUvaWRwL0lCSURQTlBTRUMvYXV0aG4iLCJkZXZpY2VpZCI6MzMyNDAsInJlc3VsdCI6MSwiYXVkIjoic3BsLWFwaSIsImFjciI6IjIiLCJ1cm46b3BlbmlkOnBhcmFtczpqd3Q6Y2xhaW06YXV0aF9yZXFfaWQiOiIxMDA2NGRhMCIsImF1dGhfdGltZSI6MTY5ODY3MTMyNywiY2xpZW50YXBwcm92YWxzdGF0dXMiOiJhY2NlcHQiLCJleHAiOjE2OTg2NzQ5MjcsImlhdCI6MTY5ODY3MTMyN30.hj29S5U9_kfibwuj_yZ2Vw8Saivo8gjTOMNSdAflcF1WISxoe4U6JMUhpXzGakK-GBWe9yNoXyMbj7go0Kfd3hd7kQfo6o2I7qifwUcprhpmM7j_sZQ_jrpN7I0M4T2tnu0GB1fhYR1NFLHoW6YhNeS7WYSc9O6m5Z2q3d7omTRreGnfZ9MUe5Tl2Jfrafu_IY7O7OHsoKln7kLecTjf6mfIlSvjgNtoUrLRK4HBnneFMP-27OwFISgz0Aoly6MDvVoXoCMkhj4RiDMOo6HDkAdl8xWdJBGAZh3Uo1pc6U3ZtOfhd1PsLZo9PM9u8rV3Bk6KeM5lSfKGQdTeAhfRuQ";
		try {
			boolean isValid = validateJWT(actividURI,idToken);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
