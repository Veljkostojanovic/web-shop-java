package com.webshop.user;

import com.webshop.Authorization.LoginRequest;
import com.webshop.Authorization.RegisterRequest;
import com.webshop.cart.Cart;
import com.webshop.common.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
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
    public User login(LoginRequest loginRequest) {

        User user = userRepository.findByUsername(loginRequest.username).orElseThrow(() -> new ResourceNotFoundException("Invalid credentials"));

        if(!passwordEncoder.matches(loginRequest.password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return user;
    }

    @Override
    public User register(RegisterRequest registerRequest) {
        if(userRepository.existsByUsername(registerRequest.username)){
            throw new IllegalArgumentException("Username already exists");
        }

        if(userRepository.existsByEmail(registerRequest.email)){
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.username);
        user.setEmail(registerRequest.email);
        user.setPassword(passwordEncoder.encode(registerRequest.password));
        user.setRole("USER");

        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);

        return userRepository.save(user);
    }
}
