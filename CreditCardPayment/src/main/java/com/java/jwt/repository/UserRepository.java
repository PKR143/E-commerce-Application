package com.java.jwt.repository;

import com.java.jwt.entity.User;
import com.java.jwt.util.AuthProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByUserName(@Param("username") String username);
//    Optional<User> findByProviderIdAndProviderType(String providerId, AuthProviderType providerType);
}
