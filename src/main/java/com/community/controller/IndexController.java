package com.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class IndexController {

	@Autowired
	RedisTemplate<String, String> redistemplate;
	
	@GetMapping("/")
	public String main() {
		return "index";
	}
	
	@GetMapping("/oauth2/redirect")
	public String oauth2Redirect() {
		return "redirect";
	}
	
	
}
