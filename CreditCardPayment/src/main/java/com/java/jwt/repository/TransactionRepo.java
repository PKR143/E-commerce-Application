package com.java.jwt.repository;

import com.java.jwt.entity.TransactionStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepo extends JpaRepository<TransactionStatusEntity ,Long> {
}
