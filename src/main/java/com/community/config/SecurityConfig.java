package com.community.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import com.community.security.oauth2.CustomOAuth2UserService;
import com.community.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	
	@Autowired
    private CustomOAuth2UserService customOAuth2UserService;
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
        .antMatchers(HttpMethod.OPTIONS, "/**")
        .antMatchers("/swagger/**","/swagger-ui.html")
        .antMatchers("/h2-console/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.cors()
			.and()
		.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
		.csrf()
			.disable()
		.formLogin()
			.disable()
		.httpBasic()
			.disable()
		.authorizeRequests()
			.antMatchers("/", "/login/**", "/error", "/favicon.ico", "/**/*.png","/**/*.gif","/**/*.svg", "/**/*.jpg","/**/*.html","/**/*.css","/**/*.js")
        	.permitAll()
        	.antMatchers("/oauth2/**", "/api/authenticate/**", "/api/register", "/auth/authenticate", "/auth/signup", "/v2/**")
        	.permitAll()
        	.anyRequest().authenticated()
        	.and()
        .logout()
            .logoutSuccessUrl("/")
            .and()
        .oauth2Login()
	        .authorizationEndpoint()
	        	.baseUri("/oauth2/authorize")
		        .authorizationRequestRepository(cookieAuthorizationRequestRepository())
		        .and()
	        .redirectionEndpoint()
		        .baseUri("/oauth2/callback/*")
		        .and()
	        .userInfoEndpoint()
		        .userService(customOAuth2UserService)
//		        .and()
//	        .successHandler(oAuth2AuthenticationSuccessHandler)
//	        .failureHandler(oAuth2AuthenticationFailureHandler)
	        ;
	}
	
	private AuthorizationRequestRepository<OAuth2AuthorizationRequest> cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }
	
}
	