package com.community.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.community.security.oauth2.userDetails.UserPrincipal;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class IndexController {

	private final HttpSession httpSession;
	
	@GetMapping("/")
	public String main(Model model) {
		UserPrincipal user = (UserPrincipal) httpSession.getAttribute("user");

		if(user != null){
			model.addAttribute("userName", user.getName());
   		}
		
		return "index";
	}
}
