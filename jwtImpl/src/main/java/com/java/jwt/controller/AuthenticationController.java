package com.java.jwt.controller;

import com.java.jwt.entity.User;
import com.java.jwt.model.AuthenticationRequest;
import com.java.jwt.model.AuthenticationResponse;
import com.java.jwt.repository.UserRepository;
import com.java.jwt.service.CustomUserDetailsService;
import com.java.jwt.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        UserDetails userDetails = null ;
        try {
            System.out.println("Try block start executing");
            String username = authenticationRequest.getUsername() ;
            String password = authenticationRequest.getPassword() ;

            userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails == null || !userDetails.getPassword().equals(password)) {
                throw new Exception("Invalid credentials");
            }

        } catch (AuthenticationException e) {
            throw new Exception("Exception Occured while Authenticate", e);
        }

        System.out.println("------------");
//        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));

    }
    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully";
    }

    @PostMapping("/jwtChecker")
    public ResponseEntity<?> checkJwt(ServletRequest request){
        HttpServletRequest servletRequest = (HttpServletRequest) request ;
        String username =  servletRequest.getHeader("username") ;
        System.out.println("The user name is " + username);
        try{
            UserDetails userDetails  = userDetailsService.loadUserByUsername(username);
            System.out.println(userDetails.toString());
            if(jwtUtil.validateToken(servletRequest.getHeader("jwt"), userDetails)){
                return ResponseEntity.ok("Valid JWT Token");
            }else{
                throw  new ExpiredJwtException(null,null,"Invalid JWT Token");
            }
        }catch (Exception e){
            System.out.println("cause is :" + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR );
        }

    }



    @GetMapping("/hello")
    public  String hello() {
        return "Hello World!";
    }
}
