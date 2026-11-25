package com.webshop.authentification;

import com.webshop.JWT.JwtService;
import com.webshop.JWT.UserPrincipal;
import com.webshop.user.UserResponse;
import com.webshop.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        if (PasswordPolicy.exceedsBcryptLimit(request.password())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return new LoginResponse(
                jwtService.issueToken(principal),
                TOKEN_TYPE,
                jwtService.getTtl().toSeconds());
    }

    public UserResponse register(RegisterRequest request) {
        PasswordPolicy.requireValid(request.password());
        String passwordHash = passwordEncoder.encode(request.password());
        return userService.createUser(request.email(), request.username(), passwordHash);
    }
}