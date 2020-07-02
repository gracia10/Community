package com.community.security.oauth2;

import static com.community.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.community.common.AuthConstants;
import com.community.common.CookieUtils;
import com.community.config.AppProperties;
import com.community.exception.BadRequestException;
import com.community.security.CustomUserDetails;
import com.community.security.jwt.TokenProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

	@Autowired
	private TokenProvider tokenProvider;
	
	@Autowired
	private AppProperties appProperties;
	
	@Autowired
	private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		String targetUrl = 	determineTargetUrl(request, response, authentication);

		if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
		
		clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
	
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);

        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
        	throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }
        
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();
        
        String accessToken = tokenProvider.createToken(createTokenMap(userPrincipal));
        String refreshToken = tokenProvider.createToken();
        CookieUtils.addRefreshCookie(response, refreshToken, appProperties.getAuth().getRefreshtokenExpirationMsec()/1000);
        
        return UriComponentsBuilder
        		.fromUriString(targetUrl)
        		.queryParam(AuthConstants.ACCESS_TOKEN_NAME, accessToken)
        		.build().toUriString();
    }
	
	private Map<String,Object> createTokenMap(CustomUserDetails userPrincipal){
        Map<String,Object> map = new HashMap<String, Object>();
        map.put(AuthConstants.CLAIM_SUBJECT, userPrincipal.getUsername());
        map.put(AuthConstants.CLAIM_EXPIRATION, new Date().getTime() + appProperties.getAuth().getTokenExpirationMsec());
        map.put(AuthConstants.CLAIM_ID, UUID.randomUUID().toString());
        map.put(AuthConstants.CLAIM_ROLE, userPrincipal.getAuthorities().stream().map(m->m.getAuthority()).collect(Collectors.joining()));
        map.put(AuthConstants.CLAIM_STATUS, userPrincipal.isEnabled());
		return map;
	}
	
	private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }
	
	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
