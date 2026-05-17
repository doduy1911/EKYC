package com.eyc.key.modules.auth.entity;

import com.eyc.key.common.enums.Role;
import com.eyc.key.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users",indexes = {
        @Index(name = "idex_users_usernmae" , columnList = "username" , unique = true),
        @Index(name = "idex_user_email" ,columnList = "email" , unique = true),
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
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

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

//    số lần đăng nhập sai
    @Column(name = "failed_login_attempts")
    @Builder.Default
    private int failedLoginAttempts = 0;
//    khóa cho đến lúc nào
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;
// thời gian tạo user
    @CreatedDate
    @Column(name = "craeted_at")
    private LocalDateTime craetedAt;
// thời gian chỉnh sủa
    @LastModifiedDate
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority("ROLE_"+role.name()));
    }

    @Override
    public String getUsername(){
        return username;
    }

    @Override
    public String getPassword(){
        return password;
    }
    @Override
    public boolean isAccountNonLocked(){
        if(lockedUntil == null && LocalDateTime.now().isBefore(lockedUntil)){
            return false;
        }
        else {
            return status != UserStatus.LOCKED && status != UserStatus.SUSPENDED;
        }
    }

    @Override
    public boolean isEnabled(){
        return status == UserStatus.ACTIVE;
    }
}
