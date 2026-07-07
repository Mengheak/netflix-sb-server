package com.example.netflix_clone.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractTokenFromHeader(request);
        if (token != null) {
            String username = jwtUtil.extractUsername(token);
            if (shouldProcessAuthentication(username)) {
                processAuthentication(request, token, username);
            }
        }

         filterChain.doFilter(request, response);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        final String authorization = request.getHeader("Authorization");
        final String requestUri = request.getRequestURI();
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }else if(requestUri.contains("/api/files/video/") || requestUri.contains("/api/files/image/") || requestUri.contains("/api/files/image/")
            && (request.getParameter("token") != null))
        {
            return request.getParameter("token");
        }
        return null;
    }

    private boolean shouldProcessAuthentication(String username) {
        return username != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }
    private void processAuthentication(HttpServletRequest request, String token, String username) {
        if(jwtUtil.validateToken(token)){
            UserDetails userDetails = createUserDetailsFromToken(token, username);
            setAuthenticationInContext(request, userDetails);
        }
    }

    private UserDetails createUserDetailsFromToken(String token, String username) {
        String role = jwtUtil.extractRole(token);
        return User.builder()
                .username(username)
                .password("")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+role)))
                .build();
    }
    private void setAuthenticationInContext(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken  authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
