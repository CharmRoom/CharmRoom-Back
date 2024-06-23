package com.charmroom.charmroom.controller.api;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class MainController {
	
	@GetMapping("/")
	@ResponseBody
	public String index(Principal principal) {
		if (principal == null)
			return "Not logged in";
		return "ID: " + principal.getName();
	}
}
