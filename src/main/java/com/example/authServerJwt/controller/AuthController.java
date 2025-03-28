package com.example.authServerJwt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.authServerJwt.dto.LoginRequest;
import com.example.authServerJwt.entity.User;
import com.example.authServerJwt.repository.UserRepository;
import com.example.authServerJwt.security.JwtUtil;
import java.util.Base64;


import java.security.PublicKey;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
	
	 // Logger for this class
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private PasswordEncoder passwordEncoder;


	// Endpoint to authenticate and generate JWT token
	/*
	 * @PostMapping("/login") public String login(@RequestParam String
	 * username, @RequestParam String password) {
	 * System.out.println("inside login"); System.out.println("inside login 1"); //
	 * Validate if username or password is null or empty if (username == null ||
	 * username.trim().isEmpty()) { logger.error("Username is null or empty.");
	 * throw new IllegalArgumentException("Username cannot be null or empty"); }
	 * else { logger.error("Username is "+username); }
	 * 
	 * if (password == null || password.trim().isEmpty()) {
	 * logger.error("Password is null or empty."); throw new
	 * IllegalArgumentException("Password cannot be null or empty"); } else {
	 * logger.error("password is "+password); } logger.info("inside login."); try {
	 * System.out.println("check 1"); logger.info("check 1"); Authentication
	 * authentication = authenticationManager .authenticate(new
	 * UsernamePasswordAuthenticationToken(username, password));
	 * System.out.println("check 2"); logger.info("check 2"); if
	 * (authentication.isAuthenticated()) { return jwtUtil.generateToken(username);
	 * // Return JWT token } else {
	 * logger.error("Authentication failed for user: {}", username); throw new
	 * RuntimeException("Invalid credentials"); } }
	 * 
	 * catch (Exception e) { // Catch any exception and provide a custom error
	 * message e.printStackTrace(); // Optional: log the exception for debugging
	 * purposes logger.error("Error during authentication for user '{}': {}",
	 * username, e.getMessage(), e); throw new
	 * RuntimeException("Error during authentication: " + e.getMessage()); }
	 * 
	 * }
	 */
	
	// Endpoint to authenticate and generate JWT token
		@PostMapping("/login")
		public String login(@RequestBody LoginRequest request) {
			System.out.println("inside login");
			System.out.println("inside login 1");
			System.out.println("request"+request.toString());
			String username = request.getUsername();
		    String password = request.getPassword();
		    System.out.println("username"+username);
		    System.out.println("password"+password);
			// Validate if username or password is null or empty
		    if (username == null || username.trim().isEmpty()) {
		        logger.error("Username is null or empty.");
		        throw new IllegalArgumentException("Username cannot be null or empty");
		    }
		    else
		    {
		    	logger.error("Username is "+username);
		    }

		    if (password == null || password.trim().isEmpty()) {
		        logger.error("Password is null or empty.");
		        throw new IllegalArgumentException("Password cannot be null or empty");
		    }
		    else
		    {
		    	logger.error("password is "+password);
		    }
			 logger.info("inside login.");
			try {
				System.out.println("check 1");
				logger.info("check 1");
				Authentication authentication = authenticationManager
						.authenticate(new UsernamePasswordAuthenticationToken(username, password));
				System.out.println("check 2");
				logger.info("check 2");
				if (authentication.isAuthenticated()) {
					return jwtUtil.generateToken(username); // Return JWT token
				} else {
					logger.error("Authentication failed for user: {}", username);
					throw new RuntimeException("Invalid credentials");
				}
			}

			catch (Exception e) {
				// Catch any exception and provide a custom error message
				e.printStackTrace(); // Optional: log the exception for debugging purposes
				logger.error("Error during authentication for user '{}': {}", username, e.getMessage(), e);
				throw new RuntimeException("Error during authentication: " + e.getMessage());
			}

		}

	// Endpoint to register a new user (optional)
	@PostMapping("/register")
	public String register(@RequestParam String username, @RequestParam String password) {

		System.out.println("Register");
		if (userRepository.findByUsername(username).isPresent()) {
			throw new RuntimeException("User already exists");
		}
		User user = new User();
		user.setUsername(username);
		//user.setPassword(password); // In a real application, hash the password!
		
		user.setPassword(passwordEncoder.encode(password));
		user.setRole("USER");
		userRepository.save(user);
		return "User registered successfully";
	}
	
	
	// Endpoint to return the public key
    @GetMapping("/public-key")
    public String getPublicKey() {
        try {
            PublicKey publicKey = jwtUtil.getPublicKey();  // Get the public key from JwtUtil

            // Encode the public key in Base64 for easy transfer
            String encodedKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());

            // Return the encoded public key as a string (you can also choose PEM format)
            return "-----BEGIN PUBLIC KEY-----\n" +
                    encodedKey + "\n" +
                    "-----END PUBLIC KEY-----";
        } catch (Exception e) {
            logger.error("Error fetching public key: {}", e.getMessage());
            throw new RuntimeException("Error fetching public key: " + e.getMessage());
        }
    }

}
