package com.github.damiansheldon.security.oauth2.client.endpoint;

import java.net.URI;
import java.util.Collections;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.web.util.UriComponentsBuilder;

public class WeworkOAuth2AuthorizationCodeGrantRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

	@Override
	public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
		ClientRegistration clientRegistration = authorizationCodeGrantRequest.getClientRegistration();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		String tokenUri = clientRegistration.getProviderDetails().getTokenUri() + "?corpid={corpid}&corpsecret={corpsecret}";
				
		URI uri = UriComponentsBuilder.fromUriString(tokenUri)
				.build(clientRegistration.getClientId(), clientRegistration.getClientSecret());

		return new RequestEntity<>(headers, HttpMethod.GET, uri);
	}

}
