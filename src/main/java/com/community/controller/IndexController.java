package com.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class IndexController {

	@GetMapping("/")
	public String main() {
		return "index";
	}
	
	@GetMapping("/oauth2/redirect")
	public String oauth2Redirect() {
		return "redirect";
	}
	
	
}
