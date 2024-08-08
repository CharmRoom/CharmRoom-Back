package com.charmroom.charmroom.controller.front;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {
	@GetMapping("/c2h")
	public String index() {
		return "c2h";
	}
}
