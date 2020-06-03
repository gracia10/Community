package com.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.community.model.domain.User;
import com.community.repository.UserRepository;
import com.community.security.oauth2.userDetails.UserPrincipal;

@RestController
public class UserController {
	
	@Autowired
    private UserRepository userRepository;
	
	@GetMapping("/user/me")
    public ResponseEntity<User> getUser(@AuthenticationPrincipal UserPrincipal principal) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
        return userRepository.findById(principal.getEmail())
                .map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
