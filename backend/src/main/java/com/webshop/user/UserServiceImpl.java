package com.webshop.user;

import com.webshop.authentification.LoginRequest;
import com.webshop.authentification.RegisterRequest;
import com.webshop.cart.Cart;
import com.webshop.cart.CartRepository;
import com.webshop.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse findUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email = " + email + " not found"));
        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse findUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User with username = " + username + " not found"));
        return UserMapper.toResponse(user);
    }

    @Override
    public void validateRegistration(String email, String userName) {
        if(userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException("Email already exists");
        }
        if(userRepository.existsByUsername(userName)) {
            throw new DataIntegrityViolationException("Username already exists");
        }
    }

    @Override
    public User login(LoginRequest loginRequest) {

        User user = userRepository.findByUsername(loginRequest.username()).orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if(!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return user;
    }

    @Override
    public User register(RegisterRequest registerRequest) {

        validateRegistration(registerRequest.email(), registerRequest.username());

        User user = new User();
        user.setUsername(registerRequest.username());
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setRole("USER");

        Cart cart = new Cart();
        cart.setUserId(user.getId());

        return userRepository.save(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toResponse).toList();
    }

    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(cartRepository.existsByUserId(id)){
            cartRepository.deleteByUserId(id);
        }
        userRepository.delete(user);
    }
}
