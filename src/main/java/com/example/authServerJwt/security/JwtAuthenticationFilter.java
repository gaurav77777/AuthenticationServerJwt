package com.example.authServerJwt.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.authServerJwt.service.CustomUserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	    private final JwtUtil jwtUtil;
	    //   private final UserDetailsService userDetailsService;
	    private final CustomUserDetailsService customUserDetailsService;

		/*
		 * public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService
		 * userDetailsService) { this.jwtUtil = jwtUtil; this.userDetailsService =
		 * userDetailsService; }
		 */
	    
	    @Autowired
	    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
	        this.jwtUtil = jwtUtil;
	        this.customUserDetailsService = customUserDetailsService;
	    }

	    @Override
	    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	            throws ServletException, IOException {
	    	
	    	String path = request.getServletPath();
	    	if (path.startsWith("/api/v1/auth/login") ||
	                path.startsWith("/api/v1/auth/register") ||
	                path.startsWith("/api/v1/auth/public-key") ||
	                path.startsWith("/h2-console")) {

	                filterChain.doFilter(request, response);
	                System.out.println("inside if:");
	                return;
	            }
	        String token = getTokenFromRequest(request);
	        
	        System.out.println("token:"+token);
	        System.out.println("StringUtils.hasText(token):"+StringUtils.hasText(token));

	        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
	            String username = jwtUtil.getUsernameFromToken(token);
	            System.out.println("username:"+username);
	            
	            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
	            System.out.println("userDetails:"+userDetails);

	            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
	                    userDetails, null, userDetails.getAuthorities());
	            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

	            SecurityContextHolder.getContext().setAuthentication(authentication);
	        }
	        else
	        {
	        	System.out.println("inside else");
	        }

	        filterChain.doFilter(request, response);
	    }

	    private String getTokenFromRequest(HttpServletRequest request) {
	        String bearerToken = request.getHeader("Authorization");
	        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
	            return bearerToken.substring(7);
	        }
	        return null;
	    }

}
