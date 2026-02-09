package com.webshop.user;

import com.webshop.common.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.getByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.getByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public void validateRegistration(String email, String userName) {
        if(userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        if(userRepository.existsByUsername(userName)) {
            throw new RuntimeException("Username already exists");
        }
    }

    @Override
    public User login(String email, String password) {
        return null;
    }
}
