package com.webshop.config;

import com.webshop.user.User;
import com.webshop.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminInitializer adminInitializer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adminInitializer, "password", "mySecretPassword");
    }

    @Test
    void createAdmin_WhenAdminDoesNotExist_ShouldCreateAndSaveAdmin() {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("mySecretPassword")).thenReturn("encodedPassword");

        adminInitializer.createAdmin();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("admin", savedUser.getUsername());
        assertEquals("admin@webshop.com", savedUser.getEmail());
        assertEquals("ADMIN", savedUser.getRole());
        assertEquals("encodedPassword", savedUser.getPassword());
    }

    @Test
    void createAdmin_WhenAdminAlreadyExists_ShouldDoNothing() {
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        adminInitializer.createAdmin();

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }
}