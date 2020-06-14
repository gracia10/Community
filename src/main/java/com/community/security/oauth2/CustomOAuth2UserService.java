package com.community.security.oauth2;

import java.util.Optional;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.community.exception.OAuth2AuthenticationProcessingException;
import com.community.model.AuthProvider;
import com.community.model.Authority;
import com.community.model.domain.User;
import com.community.repository.UserRepository;
import com.community.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService{

    private final UserRepository userRepository;
    
	@Override
	public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
	}
	
	private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
		
		String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
		OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, oAuth2User.getAttributes());
		
		if(StringUtils.isEmpty(oAuthAttributes.getEmail())) {
			throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
		}
		
		Optional<User> userOptional = userRepository.findById(oAuthAttributes.getEmail());
		
		User user;
		if(userOptional.isPresent()) {
			user = userOptional.get();
			if(!user.getProvider().equals(AuthProvider.valueOf(registrationId))) {
				throw new OAuth2AuthenticationProcessingException(user.getProvider()+" 계정이 있습니다. "+user.getProvider()+" 로그인을 이용해 주세요.");
			}
			user = updateExistingUser(user);
		} else {
			user = registerNewUser(registrationId, oAuthAttributes);
		}
        return CustomUserDetails.create(user, oAuthAttributes.getAttributes());
	}
	
	private User registerNewUser(String registrationId, OAuthAttributes oAuthAttributes) {
		
		User user = User.builder()
					.id(oAuthAttributes.getEmail())
					.name(oAuthAttributes.getName())
					.status(true)
					.authorities(Authority.ROLE_USER)
					.provider(AuthProvider.valueOf(registrationId))
					.build();
        return userRepository.save(user);
    }
	
	private User updateExistingUser(User existingUser) {
        existingUser.update();
        return userRepository.save(existingUser);
    }
}
