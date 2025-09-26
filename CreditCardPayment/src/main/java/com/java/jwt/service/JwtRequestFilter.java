//package com.java.jwt.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.java.jwt.dto.GeneralResponse;
//import com.java.jwt.util.JwtUtil;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.io.IOException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Service;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//
//@Service
//public class JwtRequestFilter extends OncePerRequestFilter {
//    @Autowired
//    private CustomUserDetailsService customUserDetailsService;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException, java.io.IOException {
//
//        String path = request.getRequestURI();
//
//        if(path.startsWith("/public/") || path.equals("/api/product/allProducts")){
//            chain.doFilter(request,response);
//            return;
//        }
//
//        logger.info("-------------- Jwt validation request received --------------------");
//
//        final String authorizationHeader = request.getHeader("Authorization");
//
//        if(authorizationHeader == null || authorizationHeader.length() <= 7){
//            logger.info("JWT is missing ");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType("application/json");
//            String json = new ObjectMapper().writeValueAsString(new GeneralResponse(null,-1L,"Jwt token is missing"));
//            response.getWriter().write(json);
//            return;
//        }
//
//        String username = null;
//        String jwt = null;
//
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            jwt = authorizationHeader.substring(7);
//            try {
//                logger.info("extracting username from jwt token");
//                username = jwtUtil.extractUsername(jwt);
//
//            }catch(ExpiredJwtException e){
//                logger.info("---jwt exception  1");
//                logger.warn(e.getMessage());
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.setContentType("application/json");
//                String json = new ObjectMapper().writeValueAsString(new GeneralResponse(null,-1L,"Jwt token is expired"));
//                response.getWriter().write(json);
//                return;
//            }catch(JwtException e){
//                logger.info("---jwt exception  2");
//                logger.warn(e.getMessage());
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.setContentType("application/json");
//                String json = new ObjectMapper().writeValueAsString(new GeneralResponse(null,-1L,"Invalid JWT token provided"));
//                response.getWriter().write(json);
//                return;
//            }
//            logger.info("username extracted successfully fromm JWT token");
//        }
//
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);
//
//            // Validate the token
//            if (jwtUtil.validateToken(jwt, userDetails)) {
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                usernamePasswordAuthenticationToken
//                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//            }
//        }
//        chain.doFilter(request, response);
//    }
//
//
//
//
//
////    public class JwtUtil {
////
////        private String secretKey = "your-secret-key"; // Replace with your actual secret key
////
////        // Method to validate the token and extract claims
////        public Claims validateAndExtractClaims(String token) {
////            try {
////                return Jwts.parser()
////                        .setSigningKey(secretKey)
////                        .parseClaimsJws(token)
////                        .getBody();
////            } catch (ExpiredJwtException e) {
////                // Handle expired token
////                System.out.println("Token has expired");
////                throw e;
////            } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
////                // Handle invalid token (invalid signature, malformed token, unsupported claims, etc.)
////                System.out.println("Invalid JWT token");
////                throw e;
////            }
////        }
////
////        // Method to check if a token is valid and return username
////        public String getUsernameFromToken(String token) {
////            Claims claims = validateAndExtractClaims(token);
////            return claims.getSubject(); // Get the username or subject from the token
////        }
////
////        // Method to check if a token is valid
////        public boolean isTokenValid(String token, String username) {
////            Claims claims = validateAndExtractClaims(token);
////            String tokenUsername = claims.getSubject();
////            return (tokenUsername.equals(username) && !isTokenExpired(token));
////        }
////
////        // Method to check if the token is expired
////        public boolean isTokenExpired(String token) {
////            Claims claims = validateAndExtractClaims(token);
////            return claims.getExpiration().before(new Date());
////        }
////    }
//
//}
