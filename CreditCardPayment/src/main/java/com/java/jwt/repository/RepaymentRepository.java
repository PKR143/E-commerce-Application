package com.java.jwt.repository;

import com.java.jwt.entity.PaymentDueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepaymentRepository extends JpaRepository<PaymentDueEntity, String> {
}
