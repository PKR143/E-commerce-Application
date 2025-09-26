package com.java.jwt.entity;

import com.java.jwt.util.AuthProviderType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "user_entity")
public class  User {
    @Column( name = "user_id")
    private String id  ;
    private String firstName ;
    private String lastName;
    private String mail;
    private String mobileNo;
    private String address;
    @Id
    @Column(unique = true )
    private String userName  ;
    private String password ;
    private String role ;

//    private String providerId;
//
//    @Enumerated(EnumType.STRING)
//    private AuthProviderType ProviderType;
}
