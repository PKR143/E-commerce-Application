package com.java.jwt.service;

import com.java.jwt.entity.User;
import com.java.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails  loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByUserName(username);
        User user = new User();
        user.setUserName("santosh");
        user.setPassword("password");
        user.setRole("ADMIN");
        user.setId(generateRandom());
        user.setName("santosh");
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        org.springframework.security.core.userdetails.User.UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(username);
        builder.password(user.getPassword());
        builder.roles(user.getRole());
        return builder.build();
    }

    private int generateRandom(){
        int min = 100000 ;
        int max = 999999;
        return  (int) (Math.random() * (max-min +1)+min);
    }
}
