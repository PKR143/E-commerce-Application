//package com.java.jwt.oAuth2;
//
//import com.java.jwt.service.AuthService;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
//
//
//    AuthService authService;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
//        OAuth2User oAuth2User = token.getPrincipal();
//
//        String registrationId = token.getAuthorizedClientRegistrationId();
//        authService.handleOAuth2LoginRequest(oAuth2User, registrationId);
//
//    }
//}
