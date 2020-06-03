package com.community.security.oauth2;

import java.util.Optional;

import javax.servlet.http.HttpSession;

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
import com.community.model.Role;
import com.community.model.domain.User;
import com.community.repository.UserRepository;
import com.community.security.oauth2.userDetails.UserPrincipal;
import com.community.security.oauth2.userInfo.OAuth2UserInfo;
import com.community.security.oauth2.userInfo.OAuth2UserInfoFactory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService{

	private final HttpSession httpSession;

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
		
		String provider = oAuth2UserRequest.getClientRegistration().getRegistrationId();
		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());
		
		if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
			throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
		}
		
		Optional<User> userOptional = userRepository.findById(oAuth2UserInfo.getEmail());
		
		User user;
		if(userOptional.isPresent()) {
			user = userOptional.get();
			if(!user.getProvider().equals(AuthProvider.valueOf(provider))) {
				throw new OAuth2AuthenticationProcessingException(user.getProvider()+" 계정이 있습니다."+user.getProvider()+" 로그인을 이용해 주세요.");
			}
			user = updateExistingUser(user);
		} else {
			user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
		}
		
		UserPrincipal principal = UserPrincipal.create(user, oAuth2User.getAttributes());
		httpSession.setAttribute("user", principal);
		
        return principal;
	}
	
	private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
		User user = User.builder()
					.email(oAuth2UserInfo.getEmail())
					.name(oAuth2UserInfo.getName())
					.status(true)
					.auth(Role.USER)
					.provider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))
					.build();
        return userRepository.save(user);
    }
	
	private User updateExistingUser(User existingUser) {
        existingUser.update();
        return userRepository.save(existingUser);
    }
}
