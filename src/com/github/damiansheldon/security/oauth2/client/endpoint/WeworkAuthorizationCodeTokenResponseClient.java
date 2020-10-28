package com.github.damiansheldon.security.oauth2.client.endpoint;

import java.time.Instant;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.github.damiansheldon.security.oauth2.http.converter.WeworkOAuth2AccessTokenResponseConverter;

public class WeworkAuthorizationCodeTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
	private static final Log log = LogFactory.getLog(WeworkAuthorizationCodeTokenResponseClient.class);

	private static final String INVALID_TOKEN_RESPONSE_ERROR_CODE = "invalid_token_response";

	private OAuth2AccessTokenResponse cachedOAuth2AccessTokenResponse;
	
	private Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> requestEntityConverter =
			new WeworkOAuth2AuthorizationCodeGrantRequestEntityConverter();

	private RestOperations restOperations;

	public WeworkAuthorizationCodeTokenResponseClient() {
		OAuth2AccessTokenResponseHttpMessageConverter converter = new OAuth2AccessTokenResponseHttpMessageConverter();
		converter.setTokenResponseConverter(new WeworkOAuth2AccessTokenResponseConverter());
		
		RestTemplate restTemplate = new RestTemplate(Arrays.asList(
				new FormHttpMessageConverter(), converter));
		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
		this.restOperations = restTemplate;
	}
	
	@Override
	public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
		Assert.notNull(authorizationGrantRequest, "authorizationCodeGrantRequest cannot be null");
		if (cachedOAuth2AccessTokenResponse != null && !isOAuth2AccessTokenExpire(cachedOAuth2AccessTokenResponse)) {			
			return cachedOAuth2AccessTokenResponse;
		}
		
		// Fetch new OAuth2 access token
		RequestEntity<?> request = this.requestEntityConverter.convert(authorizationGrantRequest);

		ResponseEntity<OAuth2AccessTokenResponse> response;
		try {
			response = this.restOperations.exchange(request, OAuth2AccessTokenResponse.class);
		} catch (RestClientException ex) {
			log.warn("获取 Access token 调用失败", ex);
			
			OAuth2Error oauth2Error = new OAuth2Error(INVALID_TOKEN_RESPONSE_ERROR_CODE,
					"An error occurred while attempting to retrieve the OAuth 2.0 Access Token Response: " + ex.getMessage(), null);
			throw new OAuth2AuthorizationException(oauth2Error, ex);
		}

		OAuth2AccessTokenResponse tokenResponse = response.getBody();

		if (CollectionUtils.isEmpty(tokenResponse.getAccessToken().getScopes())) {
			// As per spec, in Section 5.1 Successful Access Token Response
			// https://tools.ietf.org/html/rfc6749#section-5.1
			// If AccessTokenResponse.scope is empty, then default to the scope
			// originally requested by the client in the Token Request
			tokenResponse = OAuth2AccessTokenResponse.withResponse(tokenResponse)
					.scopes(authorizationGrantRequest.getClientRegistration().getScopes())
					.build();
		}
				
		cachedOAuth2AccessTokenResponse = tokenResponse;
		
		return tokenResponse;
	}
	
	private boolean isOAuth2AccessTokenExpire(OAuth2AccessTokenResponse accessTokenResponse) {
		if (accessTokenResponse == null) {
			return true;
		}
		
		if (accessTokenResponse.getAccessToken() == null) {
			return true;
		}
		
		Instant expiresAt = accessTokenResponse.getAccessToken().getExpiresAt();
		Instant now = Instant.now();

		return expiresAt.isBefore(now);
	}

}
