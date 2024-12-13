package com.example.authServerJwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

	// This endpoint is public and doesn't require JWT authentication
	@GetMapping("/public")
	public String publicEndpoint() {
		return "This is a public endpoint. No authentication required.";
	}

}
