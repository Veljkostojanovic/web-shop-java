package com.webshop.authentification;

import com.webshop.JWT.JwtService;
import com.webshop.user.User;
import com.webshop.user.UserMapper;
import com.webshop.user.UserResponse;
import com.webshop.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userService.login(loginRequest);
        String token = jwtService.generateToken(user);

        return new LoginResponse(token);
    }

    public UserResponse register(RegisterRequest registerRequest) {
        User user = userService.register(registerRequest);
        return UserMapper.toResponse(user);
    }
}
