package com.community.security.oauth2.userAttribute;

import java.util.Map;

import com.community.exception.OAuth2AuthenticationProcessingException;
import com.community.model.AuthProvider;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {

	private Map<String, Object> attributes;
	private String nameAttributeKey;
	private String name;
	private String email;

	@Builder
	public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email) {
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.name = name;
		this.email = email;
	}

	public static OAuthAttributes of(String registrationId, Map<String, Object> attributes) {

		if (registrationId.equalsIgnoreCase(AuthProvider.google.toString())) {
			return ofGoogle(attributes);
		} else if (registrationId.equalsIgnoreCase(AuthProvider.naver.toString())) {
			return ofNaver(attributes);
		} else {
			throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
		}

	}

	private static OAuthAttributes ofGoogle(Map<String, Object> attributes) {
		return OAuthAttributes.builder()
				.name((String) attributes.get("name"))
				.email((String) attributes.get("email"))
				.attributes(attributes)
				.nameAttributeKey("sub")
				.build();
	}

	private static OAuthAttributes ofNaver(Map<String, Object> attributes) {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");

		return OAuthAttributes.builder()
				.name((String) response.get("name"))
				.email((String) response.get("email"))
				.attributes(response)
				.nameAttributeKey("response")
				.build();
	}

}
