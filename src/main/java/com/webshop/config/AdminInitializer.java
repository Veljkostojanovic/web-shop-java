package com.webshop.config;

import com.webshop.user.User;
import com.webshop.user.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Value("${admin.password}")
    private String password;

    @PostConstruct
    public void createAdmin(){
        if(!userRepository.existsByUsername("admin")){
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@webshop.com");
            admin.setRole("ADMIN");
            admin.setPassword(passwordEncoder.encode(password));
            userRepository.save(admin);
        }
    }


}
