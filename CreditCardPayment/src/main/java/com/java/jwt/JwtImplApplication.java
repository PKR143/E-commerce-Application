package com.java.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JwtImplApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtImplApplication.class, args);
	}

}
