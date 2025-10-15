package com.java.jwt.exception;

import com.java.jwt.dto.GeneralResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.security.SignatureException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handleOrderException(PaymentException e){
        logger.info("handling PaymentException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GeneralResponse(null, -1L, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        logger.info("handling MethodArgumentNotValidException");
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error-> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GeneralResponse(null,-1L,errors.values().toString()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        logger.info("handling HttpMessageNotReadableException");
        e.printStackTrace();
        String[] error = e.getMessage().split(":");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GeneralResponse(null, -1L,error[0]));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwt(ExpiredJwtException ex) {
        logger.info("handling ExpiredJwtException");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new GeneralResponse(null, -1L, "Jwt Token expired"));
    }
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleExpiredJwt(JwtException ex) {
        logger.info("handling JwtException");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new GeneralResponse(null, -1L, "invalid Jwt Token provided"));
    }
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String,String>> handleSignatureException(SignatureException ex) {
        logger.info("handling SignatureException");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error","Invalid JWT signature"));
    }
    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException e){
        logger.info("handling IO Exception");
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new GeneralResponse(null, -1L, "Something went wrong, please try again!"));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> Exception(Exception e){
        logger.info("Exception @{} due to: {}",e.getClass(),e.getMessage());
        return ResponseEntity.badRequest().body(new GeneralResponse(null,-1L,e.getMessage()));
    }
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<?> RuntimeException(RuntimeException e){
//        logger.info("RuntimeException @{} due to: {}",e.getClass(),e.getMessage());
//        return ResponseEntity.badRequest().body(new GeneralResponse(null,-1L,e.getMessage()));
//    }
}
