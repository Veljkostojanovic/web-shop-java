package com.webshop.user;

public interface UserService {
    User getUserByEmail(String email);
    User getUserByUsername(String username);
    void validateRegistration(String email, String userName);
    User login(String email, String password);
}
