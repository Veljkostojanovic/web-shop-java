package com.webshop.user;

import com.webshop.common.util.TextNormalize;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_email",
                        columnNames = "email"
                ),
                @UniqueConstraint(
                        name = "uk_users_username",
                        columnNames = "username"
                )
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    public User(String email, String username, String passwordHash, UserRole role) {
        changeEmail(email);
        changeUsername(username);
        changePassword(passwordHash);
        changeRole(role);
    }

    private void changeEmail(String email){
        email = TextNormalize.normalize(email);
        if(email == null || email.isBlank()){
            throw new IllegalArgumentException("Email is required");
        }
        this.email = email;
    }

    private void changeUsername(String username){
        username = TextNormalize.normalize(username);
        if(username == null || username.isBlank()){
            throw new  IllegalArgumentException("Username is required");
        }
        this.username = username;
    }

    private void changePassword(String passwordHash){
        if(passwordHash == null || passwordHash.isBlank()){
            throw new IllegalArgumentException("Password is required");
        }
        this.passwordHash = passwordHash;
    }

    private void changeRole(UserRole role){
        if(role == null){
            throw new IllegalArgumentException("Role is required");
        }
        this.role = role;
    }
}
