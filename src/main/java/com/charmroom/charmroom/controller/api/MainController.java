package com.charmroom.charmroom.viewcontroller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class IndexController {
	
	@GetMapping("/")
	@ResponseBody
	public String index(Principal principal) {
		if (principal == null)
			return "Not logged in";
		return "ID: " + principal.getName();
	}
}
