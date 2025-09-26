package com.java.jwt.controller;

import com.java.jwt.entity.User;
import com.java.jwt.dto.AuthenticationRequest;
import com.java.jwt.dto.AuthenticationResponse;
import com.java.jwt.dto.GeneralResponse;
import com.java.jwt.dto.UserResponse;
import com.java.jwt.repository.UserRepository;
import com.java.jwt.service.CustomUserDetailsService;
import com.java.jwt.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


@RestController
@RequestMapping("/public")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        UserDetails userDetails = null ;
        try {
            System.out.println("Try block start executing");
            String username = authenticationRequest.getUsername() ;
            String password = authenticationRequest.getPassword() ;
            userDetails = userDetailsService.loadUserByUsername(username);
//            System.out.println(userDetails.toString()+", "+userDetails.getUsername()+", "+userDetails.getPassword());
            logger.info("userDetails().toString(): {}",userDetails.toString());
            logger.info("username fetched from DB: {}",userDetails.getUsername());
            logger.info("password fetched from DB: {}",userDetails.getPassword());
//            if (userDetails == null || !userDetails.getPassword().equals(password)) {
////                throw new Exception("Invalid credentials");
//                return  new ResponseEntity<>("Invalid credentials" , HttpStatus.BAD_REQUEST);
//            }
            Authentication res = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            if(res.isAuthenticated()){
                logger.info(username+" user is authenticated");

            }else{
                logger.info(username+" user is not authenticated");
            }
            logger.info("Authentication response: {}",res.toString());
        } catch (Exception e) {
//            throw new Exception("Exception Occured while Authentication", e);
            return  new ResponseEntity<>(new GeneralResponse(null, -1L,"Invalid Credentials") , HttpStatus.BAD_REQUEST);
        }

        System.out.println("------------");
//        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        logger.info("jwt generated: {}",jwt);
        return ResponseEntity.ok(new GeneralResponse(new AuthenticationResponse(jwt), 1L, "Login successful!"));

    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {

        User exist = userRepository.findByUserName(user.getUserName());

        if(exist != null){
            logger.info("Username already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GeneralResponse(null, -1L, "Username already exists"));
        }

        user.setId(generateId());
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(new GeneralResponse(new UserResponse(savedUser), 1L, "Successfully Registered, Now please login to continue."));
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
//                return ResponseEntity.ok("Valid JWT Token");
                return ResponseEntity.ok(new GeneralResponse(new AuthenticationResponse(servletRequest.getHeader("jwt")), 1L, "Valid JWT Token"));
            }else{
                throw  new ExpiredJwtException(null,null,"Invalid JWT Token");
            }
        }catch (Exception e){
            System.out.println("cause is :" + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR );
        }

    }



    @GetMapping("/profile")
    public Map<String, Object> getUserProfile(JwtAuthenticationToken token) {
        if (token == null) {
            throw new RuntimeException("User is not authenticated.");
        }
        return token.getTokenAttributes(); // contains email, sub, aud, iss, etc.
    }

    @GetMapping("/home")
    public  String hello() {
        return "Home Page!";
    }

    public String generateId(){

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmssSS");
        String datetime = ft.format(dNow);
        return datetime;
    }
}
