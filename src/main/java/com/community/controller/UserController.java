package com.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.community.model.domain.User;
import com.community.repository.UserRepository;
import com.community.security.CustomUserDetails;

@RestController
public class UserController {
	
	@Autowired
    private UserRepository userRepository;
	
	@GetMapping("/user/me")
	@PreAuthorize("hasAnyRole('USER, ADMIN')")
    public ResponseEntity<User> getUser(@AuthenticationPrincipal CustomUserDetails principal) {
		
        return userRepository.findById(principal.getEmail())
                .map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
	
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
    public String dispAdmin() {
        return "hello world it's admin";
    }
}
