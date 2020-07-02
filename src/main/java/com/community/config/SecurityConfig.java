package com.community.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.community.security.CustomUserDetailsService;
import com.community.security.jwt.JwtAuthenticationEntryPoint;
import com.community.security.jwt.TokenAuthenticationFilter;
import com.community.security.oauth2.CustomOAuth2UserService;
import com.community.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.community.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.community.security.oauth2.OAuth2AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
    private CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	private CustomOAuth2UserService customOAuth2UserService;
	
	@Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;
	
	@Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	
	@Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
	
	@Autowired
	private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

	@Bean
	public AuthorizationRequestRepository<OAuth2AuthorizationRequest> cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

	@Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }
	
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
		.httpBasic()
			.disable()
		.cors()
			.and()
		.csrf()
			.disable()
		.exceptionHandling()
            .authenticationEntryPoint(unauthorizedHandler)
            .and()
		.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
		.formLogin()
			.disable()
		.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
		.authorizeRequests()
			.antMatchers("/", "/login/**", "/error", "/favicon.ico", "/**/*.png","/**/*.gif","/**/*.svg", "/**/*.jpg","/**/*.html","/**/*.css","/**/*.js")
        	.permitAll()
        	.antMatchers("/oauth2/**","/auth/**", "/v2/**")
        	.permitAll()
        	.anyRequest().authenticated()
        	.and()
        .oauth2Login()
	        .authorizationEndpoint()
	        	.baseUri("/oauth2/authorize")
		        .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
		        .and()
	        .redirectionEndpoint()
		        .baseUri("/oauth2/callback/*")
		        .and()
	        .userInfoEndpoint()
		        .userService(customOAuth2UserService)
		        .and()
	        .successHandler(oAuth2AuthenticationSuccessHandler)
	        .failureHandler(oAuth2AuthenticationFailureHandler)
	        ;
		
	}
	
	
}
	