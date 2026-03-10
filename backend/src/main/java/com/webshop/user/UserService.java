package com.webshop.user;

import com.webshop.authorization.LoginRequest;
import com.webshop.authorization.RegisterRequest;

import java.util.List;

public interface UserService {
    UserDTO findUserByEmail(String email);
    UserDTO findUserByUsername(String username);
    void validateRegistration(String email, String userName);
    User login(LoginRequest loginRequest);
    User register(RegisterRequest registerRequest);
    List<UserDTO> getAllUsers();
    void deleteUserById(Long id);
}
