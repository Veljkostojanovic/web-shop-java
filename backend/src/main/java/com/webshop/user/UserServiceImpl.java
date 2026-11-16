package com.webshop.user;

import com.webshop.authentification.LoginRequest;
import com.webshop.authentification.RegisterRequest;
import com.webshop.cart.Cart;
import com.webshop.cart.CartRepository;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.common.util.TextNormalize;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @PreAuthorize("isAuthenticated()")
    public UserResponse getCurrentUser(){
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return UserMapper.toResponse(requireUserByUsername(username));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse findUserByEmail(String email) {
        email = TextNormalize.normalize(email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse findUserByUsername(String username) {
        return UserMapper.toResponse(requireUserByUsername(username));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::toResponse);
    }

    @Override
    public void validateRegistration(String email, String username) {
        if(userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException("Email already exists");
        }
        if(userRepository.existsByUsername(username)) {
            throw new DataIntegrityViolationException("Username already exists");
        }
    }

    @Override
    public User login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.username()).orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if(!passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return user;
    }

    @Override
    @Transactional
    public User register(RegisterRequest registerRequest) {
        validateRegistration(registerRequest.email(), registerRequest.username());

        User user = new User(
                registerRequest.email(),
                registerRequest.username(),
                passwordEncoder.encode(registerRequest.password()),
                UserRole.USER
        );

        User saved = userRepository.save(user);
        cartRepository.save(new Cart(saved));

        return saved;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        cartRepository.deleteByUserId(user.getId());
        cartRepository.flush();


        userRepository.delete(user);
        userRepository.flush();
    }

    private User requireUserByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
