package com.java.jwt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "user_details_jwt")
public class  User {
    @Column( name = "user_id")
    private int id  ;
    private String name ;
    @Id
    @Column(unique = true )
    private String userName  ;
    private String password ;
    private String role ;
}
