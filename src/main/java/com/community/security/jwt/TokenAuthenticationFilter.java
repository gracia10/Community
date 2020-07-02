package com.community.security.jwt;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.community.common.AuthConstants;
import com.community.common.CookieUtils;
import com.community.model.domain.User;
import com.community.security.CustomUserDetails;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
    private TokenProvider tokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			String jwt = getJwtFromRequest(request);
            if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            	User user = tokenProvider.getUserFromToken(jwt);
            	UserDetails userDetails = CustomUserDetails.create(user);
            	UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            	authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            	SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, AuthConstants.NOT_AUTHORISED + ex.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
	}
	
	public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AuthConstants.AUTH_KEY);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
	}
	
}
