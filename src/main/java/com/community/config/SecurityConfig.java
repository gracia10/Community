package com.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.community.security.oauth2.CustomOAuth2UserService;
import com.community.security.oauth2.userDetails.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    private final CustomOAuth2UserService customOAuth2UserService;
    
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService);
    }
    
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
//		.sessionManagement()
//			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//			.and()
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
//		        .authorizationRequestRepository(cookieAuthorizationRequestRepository())
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
	
//	private AuthorizationRequestRepository<OAuth2AuthorizationRequest> cookieAuthorizationRequestRepository() {
//        return new HttpCookieOAuth2AuthorizationRequestRepository();
//    }
	
}
	