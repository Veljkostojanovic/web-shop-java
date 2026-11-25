package com.webshop.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse createUser(String email, String username, String passwordHash);

    UserResponse getCurrentUser(Long userId);

    UserResponse findUserByEmail(String email);

    UserResponse findUserByUsername(String username);

    Page<UserResponse> getAllUsers(Pageable pageable);

    void disableUser(Long userId, Long actingAdminId);

    void enableUser(Long userId);
}