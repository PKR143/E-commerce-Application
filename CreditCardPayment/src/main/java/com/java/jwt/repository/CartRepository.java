package com.java.jwt.repository;

import com.java.jwt.entity.UserCartDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<UserCartDetailsEntity,String> {
}
