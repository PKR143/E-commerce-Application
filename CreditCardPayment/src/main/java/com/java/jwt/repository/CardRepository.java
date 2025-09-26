package com.java.jwt.repository;

import com.java.jwt.entity.CardDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<CardDetailsEntity, String> {
    Optional<CardDetailsEntity> findByCardNumberAndUserName(String cardNumber, String username);
}
