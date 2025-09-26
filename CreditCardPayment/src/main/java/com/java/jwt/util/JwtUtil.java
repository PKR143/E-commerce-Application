package com.java.jwt.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtUtil {

    private final String SECRET_KEY = "asjdofboaibdkfkkkijdqwedsdsnvdsanoifnqijdiqjefvq";

    public JwtUtil() throws NoSuchAlgorithmException {
    }

    public String extractUsername(String token)throws ExpiredJwtException  {
        return extractClaim(token, Claims::getSubject);
    }


    public Date extractExpiration(String token)throws ExpiredJwtException  {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws ExpiredJwtException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws ExpiredJwtException {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) throws ExpiredJwtException {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

//    public String generateToken(UserDetails userDetails) {
//        return Jwts.builder()
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//                .compact();
//    }
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


//    public static  String generateSecretKey() throws NoSuchAlgorithmException {
//        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
//        keyGen.init(256); // Specify the key size
//        Key key = keyGen.generateKey();
//
//        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
//        System.out.println("Generated Key: " + encodedKey);
//
//        return  encodedKey ;
//    }
}
