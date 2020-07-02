package com.community.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.community.common.AuthConstants;
import com.community.common.CookieUtils;
import com.community.config.AppProperties;
import com.community.model.domain.User;
import com.community.repository.UserRepository;
import com.community.security.CustomUserDetails;
import com.community.security.jwt.TokenProvider;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UserController {
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private TokenProvider tokenProvider;
	
	@Autowired
	private AppProperties appProperties;
	
	
	@GetMapping("/user/me")
	@PreAuthorize("hasAnyRole('USER, ADMIN')")
    public ResponseEntity<User> getUser(@AuthenticationPrincipal CustomUserDetails principal) {
        return userRepository.findById(principal.getUsername())
                .map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
	
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
    public String dispAdmin() {
        return "hello world it's admin";
    }
	
	@PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> m,HttpServletRequest request, HttpServletResponse response) {
		
		String acceessToken = m.get(AuthConstants.ACCESS_TOKEN_NAME);
		String refreshToken = CookieUtils.getCookie(request, AuthConstants.REFESH_COOKIE_NAME).map(Cookie::getValue).orElse(null);
		
		List<String> token = new ArrayList<String>();
		token.add(acceessToken);
		token.add(refreshToken);
		
		token.forEach(e -> {
			if(StringUtils.hasText(e)) {
				Map<String,Object> claims = tokenProvider.getClaimsFromDecodToken(e);
				
				long restTime = Long.valueOf(claims.get("exp").toString()) - System.currentTimeMillis()/1000;
				if(restTime>0) {
					tokenProvider.setIdBlackList(claims.get("jti").toString(),restTime);
				}
		    }
		});
		
		CookieUtils.deleteCookie(request, response, AuthConstants.REFESH_COOKIE_NAME);
		return new ResponseEntity<>(HttpStatus.OK);
    }
	
	
	@PostMapping("/auth/refresh")
    public ResponseEntity<String> refresh(@RequestBody Map<String, String> m,HttpServletRequest request, HttpServletResponse response) {
		
		String acceessToken = m.get(AuthConstants.ACCESS_TOKEN_NAME);
		String refreshToken = CookieUtils.getCookie(request, AuthConstants.REFESH_COOKIE_NAME).map(Cookie::getValue).orElse(null);
		
		if(StringUtils.isEmpty(acceessToken)) {
			log.error(AuthConstants.EMPTY_ACCESS_TOKEN);
			return new ResponseEntity<>(AuthConstants.EMPTY_ACCESS_TOKEN,HttpStatus.UNAUTHORIZED);
		}

		try {
			if(tokenProvider.validateToken(refreshToken)) {

				List<String> token = new ArrayList<String>();
				token.add(acceessToken);
				token.add(refreshToken);
				
				token.forEach(e -> {
					if(StringUtils.hasText(e)) {
						Map<String, Object> claims = tokenProvider.getClaimsFromDecodToken(e);
						long restTime = Long.valueOf(claims.get("exp").toString()) - System.currentTimeMillis()/1000;
						if(restTime>0) {
							tokenProvider.setIdBlackList(claims.get("jti").toString(),restTime);
						}
				    }
				});
				
				Map<String,Object> pastClaims = tokenProvider.getClaimsFromDecodToken(token.get(0));
				pastClaims.put(AuthConstants.CLAIM_EXPIRATION, new Date().getTime() + appProperties.getAuth().getTokenExpirationMsec());
				pastClaims.put(AuthConstants.CLAIM_ID, UUID.randomUUID().toString());

				acceessToken = tokenProvider.createToken(pastClaims);
		        refreshToken = tokenProvider.createToken();
		        CookieUtils.addRefreshCookie(response, refreshToken, appProperties.getAuth().getRefreshtokenExpirationMsec()/1000);				
				
		    }
			
		} catch (Exception e) {
			log.error("Could not refresh token"+e.getMessage());
			return new ResponseEntity<>(e.getMessage(),HttpStatus.UNAUTHORIZED);
		}finally {
			CookieUtils.deleteCookie(request, response, AuthConstants.REFESH_COOKIE_NAME);
		}
		
		return new ResponseEntity<>(acceessToken, HttpStatus.OK);
    }
	
	
}
