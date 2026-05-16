package com.eyc.key.modules.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.beans.ConstructorProperties;
import java.util.UUID;

@Entity
@Table(name = "users",indexes = {
        @Index(name = "idex_users_usernmae" , columnList = "username" , unique = true),
        @Index(name = "idex_user_email" ,columnList = "email" , unique = true),
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId ;

    @Column(nullable = false , unique = false , length = 50)
    private String username;

    @Column(nullable = false ,unique = false , length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false , length = 50)
    private String full_name;

    @Column(name = "phone_number" , nullable = false , length = 10)
    private String phone_number;

    @Column(name = "address" , nullable = false , length = 500)
    private String address;



}
