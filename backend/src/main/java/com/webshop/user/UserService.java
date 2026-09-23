package com.webshop.user;

import com.webshop.authentification.LoginRequest;
import com.webshop.authentification.RegisterRequest;
import java.util.List;

public interface UserService {
    UserResponse findUserByEmail(String email);
    UserResponse findUserByUsername(String username);
    void validateRegistration(String email, String userName);
    User login(LoginRequest loginRequest);
    User register(RegisterRequest registerRequest);
    List<UserResponse> getAllUsers();
    void deleteUserById(Long id);
}
