package com.webshop.user;

import com.webshop.authentification.LoginRequest;
import com.webshop.authentification.RegisterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse findUserByEmail(String email);
    UserResponse findUserByUsername(String username);
    void validateRegistration(String email, String userName);
    User login(LoginRequest loginRequest);
    User register(RegisterRequest registerRequest);
    UserResponse getCurrentUser();
    Page<UserResponse> getAllUsers(Pageable pageable);
    void deleteUserById(Long id);
}
