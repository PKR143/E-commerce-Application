package com.java.jwt.util;

import com.java.jwt.exception.PaymentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Slf4j
public class AuthUtil {

    public static AuthProviderType getAuthProviderTypeFromRegistrationId(String registrationId){
        return switch(registrationId.toUpperCase()){
            case "GOOGLE" -> AuthProviderType.GOOGLE;
            case "GITHUB" -> AuthProviderType.GITHUB;
            default -> throw new PaymentException("invalid registration Id");
        };
    }

    public static String getAuthProviderIdFromOAuth2User(OAuth2User oAuth2User, String registrationId) {

        String providerId = switch(registrationId.toUpperCase()){
            case "GOOGLE" -> oAuth2User.getAttribute("sub");
            case "GITHUB" -> oAuth2User.getAttribute("id");
            default -> {
                log.error("Unsupported oAuth2 provider");
                throw new PaymentException("Unsupported OAuth2 Provider");
            }
        };

        if(providerId == null || providerId.isBlank()){
            log.error("unable to find providerId from the registrationId");
            throw new PaymentException("unable to find providerId from the registrationId");
        }
        return providerId;

    }
}
