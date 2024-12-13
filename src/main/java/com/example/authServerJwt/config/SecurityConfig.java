package com.example.authServerJwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.authServerJwt.security.JwtAuthenticationFilter;
import com.example.authServerJwt.security.JwtUtil;
import com.example.authServerJwt.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {
	
	private final JwtUtil jwtUtil; // Injected JWT utility
	 // Inject your custom UserDetailsService
    private final CustomUserDetailsService customUserDetailsService;
    
    public SecurityConfig(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	/*
	 * @Bean public AuthenticationManager
	 * authenticationManager(AuthenticationConfiguration
	 * authenticationConfiguration) throws Exception { return
	 * authenticationConfiguration.getAuthenticationManager(); }
	 */
    
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                   .userDetailsService(userDetailsService())  // Make sure this points to your UserDetailsService
                   .passwordEncoder(passwordEncoder())  // Ensure that the correct password encoder is used
                   .and()
                   .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configure the HTTP security rules using the updated API
        http.csrf().disable()
            .authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()  // Allow access to the H2 console
                .antMatchers("/api/v1/auth/login", "/api/v1/auth/register").permitAll()  // Allow open access to login/register
                .anyRequest().authenticated()  // Protect all other endpoints
            .and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, customUserDetailsService), UsernamePasswordAuthenticationFilter.class) // Register JWT filter
            .formLogin().disable();  // Disable form login (for stateless JWT authentication)
        
        // Allow H2 console to be displayed in an iframe
        http.headers().frameOptions().sameOrigin();  // Allows H2 console to be embedded in an iframe

        return http.build();  // Return the configured HTTP security filter chain
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService; // Use the custom UserDetailsService
    }
}
