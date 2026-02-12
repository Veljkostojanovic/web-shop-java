package com.webshop.user;

import com.webshop.Authorization.LoginRequest;
import com.webshop.Authorization.RegisterRequest;

public interface UserService {
    User findUserByEmail(String email);
    User findUserByUsername(String username);
    void validateRegistration(String email, String userName);
    User login(LoginRequest loginRequest);
    User register(RegisterRequest registerRequest);
}
