package com.webshop.user;

import com.webshop.common.exceptions.DomainValidationException;
import com.webshop.common.persistence.AuditableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends AuditableEntity {

    private static final int PASSWORD_HASH_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = UserIdentity.MAX_EMAIL_LENGTH)
    private String email;

    @Column(nullable = false, length = UserIdentity.MAX_USERNAME_LENGTH)
    private String username;

    @Column(name = "password_hash", nullable = false, length = PASSWORD_HASH_LENGTH)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Version
    @Column(nullable = false)
    private Long version;

    public User(String email, String username, String passwordHash, UserRole role) {
        changeEmail(email);
        this.username = UserIdentity.requireValidUsername(username);
        changePasswordHash(passwordHash);
        changeRole(role);
        this.status = UserStatus.ACTIVE;
    }

    private void changeEmail(String email) {
        this.email = UserIdentity.requireValidEmail(email);
    }

    private void changePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new DomainValidationException("Password hash is required");
        }
        this.passwordHash = passwordHash;
    }

    private void changeRole(UserRole role) {
        if (role == null) {
            throw new DomainValidationException("Role is required");
        }
        this.role = role;
    }

    public void disable() {
        this.status = UserStatus.DISABLED;
    }

    public void enable() {
        this.status = UserStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
}
