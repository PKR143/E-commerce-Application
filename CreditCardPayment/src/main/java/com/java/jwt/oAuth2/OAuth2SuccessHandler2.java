package com.java.jwt.oAuth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.jwt.dto.AuthenticationResponse;
import com.java.jwt.dto.GeneralResponse;
import com.java.jwt.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2SuccessHandler2 extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);
    }


//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
//        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
//        String username = oAuth2User.getAttribute("email");
//
//        String token = jwtUtil.generateToken(username);
//        log.info("Generated token for oauth2 user: {}",token);
//        response.setContentType("application/json");
//        response.getWriter().write(new ObjectMapper().writeValueAsString(new GeneralResponse(new AuthenticationResponse(token), 1L, "Login Successful")));
//        chain.doFilter(request,response);
//    }

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//
//        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
//        String username = oAuth2User.getAttribute("email");
//
//        String token = jwtUtil.generateToken(username);
//        log.info("Generated token for oauth2 user: {}",token);
//        response.setContentType("application/json");
//        response.getWriter().write(new ObjectMapper().writeValueAsString(new GeneralResponse(new AuthenticationResponse(token), 1L, "Login Successful")));
//    }
}
