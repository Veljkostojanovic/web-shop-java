package com.webshop.config;

import com.webshop.authentification.PasswordPolicy;
import com.webshop.user.User;
import com.webshop.user.UserIdentity;
import com.webshop.user.UserRepository;
import com.webshop.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username:admin}")
    private String username;

    @Value("${admin.email:admin@webshop.com}")
    private String email;

    @Value("${admin.password}")
    private String password;

    @Override
    public void run(ApplicationArguments args) {
        String normalizedUsername = UserIdentity.normalizeUsername(username);
        if (userRepository.existsByUsername(normalizedUsername)) {
            return;
        }

        if (password.length() < PasswordPolicy.MIN_LENGTH) {
            throw new IllegalStateException(
                    "admin.password must be at least " + PasswordPolicy.MIN_LENGTH + " characters");
        }
        PasswordPolicy.requireValid(password);

        try {
            userRepository.saveAndFlush(new User(email, normalizedUsername,
                    passwordEncoder.encode(password), UserRole.ADMIN));
            log.info("Initial admin account created: username={}", normalizedUsername);
        } catch (DataIntegrityViolationException e) {
            log.info("Initial admin account already created by another instance");
        }
    }
}