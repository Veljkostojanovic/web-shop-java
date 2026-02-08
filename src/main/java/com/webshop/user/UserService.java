package com.webshop.user;

public interface UserService {
    UserDTO getUserByEmail(String email);
    UserDTO getUserByUsername(String username);
}
