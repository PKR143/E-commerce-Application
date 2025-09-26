//package com.java.jwt.service;
//
//import com.java.jwt.entity.User;
//import com.java.jwt.repository.UserRepository;
//import com.java.jwt.util.AuthProviderType;
//import com.java.jwt.util.AuthUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//
//    UserRepository userRepository;
//
//    public ResponseEntity<?> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId){
//        AuthProviderType authProviderType = AuthUtil.getAuthProviderTypeFromRegistrationId(registrationId);
//        String providerId = AuthUtil.getAuthProviderIdFromOAuth2User(oAuth2User, registrationId);
//
//        User user = userRepository.findByProviderIdAndProviderType(providerId, authProviderType).orElse(null);
//
//
//    }
//
//    public String generateId(){
//
//        Date dNow = new Date();
//        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmssSS");
//        String datetime = ft.format(dNow);
//        return datetime;
//    }
//
//}
