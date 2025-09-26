package com.java.jwt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.jwt.dto.GeneralResponse;
import com.java.jwt.oAuth2.OAuth2SuccessHandler2;
import com.java.jwt.service.CustomUserDetailsService;
//import com.java.jwt.service.JwtRequestFilter;
//import com.java.jwt.service.JwtRequestFilter;
import com.java.jwt.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@Slf4j
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

//    @Autowired
//    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private OAuth2SuccessHandler2 oAuth2SuccessHandler;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    String GOOGLE_CLIENT_ID;

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http.csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/public/**","/api/product/allProducts").permitAll()
//                        .requestMatchers("/api/product/**").hasRole("ADMIN")
//                        .requestMatchers("/api/**").authenticated()
//                        .anyRequest().authenticated()
//                )
////                .sessionManagement(session -> session
////                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
//
////                .oauth2Login(oAuth2 -> oAuth2
////                        .successHandler(oAuth2SuccessHandler)
////                        .failureHandler(
////                        (request, response, exception) -> {
////                            log.error("OAuth2 error: {}",exception.getMessage());
////                        }
////                        )
////
////                )
//
//                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }



@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/profile").authenticated()
                    .requestMatchers("/api/**").authenticated()
                    .anyRequest().permitAll()
            )
            // Enable login with Google (browser redirect)
            .oauth2Login(Customizer.withDefaults())
            // Enable API protection using ID tokens
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(Customizer.withDefaults())
                    .authenticationEntryPoint(((request, response, authException) -> {
                        response.setContentType("application/json");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write(new ObjectMapper().writeValueAsString(new GeneralResponse(null, -1L, "invalid credentials")));
                    }
                    ))
            );

    return http.build();
}
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/profile").authenticated()
//                        .requestMatchers("/api/**").authenticated()
//                        .anyRequest().permitAll()
//                )
//                // Enable login with Google (browser redirect)
//                .oauth2Login(Customizer.withDefaults())
//                // Enable API protection using ID tokens
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
//
//        return http.build();
//    }



    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withJwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .build();

        // Validate issuer and audience
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer("https://accounts.google.com");
        OAuth2TokenValidator<Jwt> withAudience = new AudienceValidator(GOOGLE_CLIENT_ID);
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience);

        jwtDecoder.setJwtValidator(validator);

        return jwtDecoder;
    }

    public static class AudienceValidator implements OAuth2TokenValidator<Jwt> {
        private final String audience;

        public AudienceValidator(String audience) {
            this.audience = audience;
        }

        @Override
        public OAuth2TokenValidatorResult validate(Jwt jwt) {
            if (jwt.getAudience().contains(audience)) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "The required audience is missing", null));
        }
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

}