package com.webshop.user;

import com.webshop.authorization.LoginRequest;
import com.webshop.authorization.RegisterRequest;
import com.webshop.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository  userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private UserDTO userDTO;
    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp(){
        userDTO = new UserDTO(1L, "user", "user@gmail.com", "USER");
        user = new User(1L, "user@gmail.com", "user", "Password", "USER");
        registerRequest = new RegisterRequest("user", "user@gmail.com", "password");
        loginRequest = new LoginRequest("user", "password");
    }


    @Nested
    @DisplayName("Find user Tests")
    class FindUserTests {

        @Test
        void findUserByEmail_Ok(){
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

            UserDTO result = userServiceImpl.findUserByEmail("user@gmail.com");

            Assertions.assertNotNull(result);
            Assertions.assertEquals(result.getEmail(), userDTO.getEmail());
            Assertions.assertEquals(result.getUsername(), userDTO.getUsername());
            Assertions.assertEquals(result.getRole(), userDTO.getRole());

            verify(userRepository).findByEmail(anyString());
        }

        @Test
        void findUserByEmail_shouldThrowResourceNotFoundException(){
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = Assertions.assertThrows(
                    ResourceNotFoundException.class, () -> userServiceImpl.findUserByEmail("user@gmail.com")
            );

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("User not found", exception.getMessage());

            verify(userRepository).findByEmail(anyString());
        }

        @Test
        void findUserByUsername_Ok(){
            when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

            UserDTO result = userServiceImpl.findUserByUsername("user@gmail.com");

            Assertions.assertNotNull(result);
            Assertions.assertEquals(result.getEmail(), userDTO.getEmail());
            Assertions.assertEquals(result.getUsername(), userDTO.getUsername());
            Assertions.assertEquals(result.getRole(), userDTO.getRole());

            verify(userRepository).findByUsername(anyString());
        }

        @Test
        void findUserByUsername_shouldThrowResourceNotFoundException(){
            when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = Assertions.assertThrows(
                    ResourceNotFoundException.class, () -> userServiceImpl.findUserByUsername("user")
            );

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("User not found", exception.getMessage());

            verify(userRepository).findByUsername(anyString());
        }

        @Test
        void getAllUsers_shouldReturnListOfUsers(){
            when(userRepository.findAll()).thenReturn(List.of(user));

            List<UserDTO> result = userServiceImpl.getAllUsers();

            Assertions.assertNotNull(result);
            Assertions.assertEquals(result.getFirst().getEmail(), userDTO.getEmail());
            Assertions.assertEquals(result.getFirst().getUsername(), userDTO.getUsername());
            Assertions.assertEquals(result.getFirst().getRole(), userDTO.getRole());
            verify(userRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Validation, login and Register tests")
    class ValidationTests {

        @Test
        void validateRegistration_shouldThrow_DataIntegrityValidationException_EmailExists()
                throws Exception{

            when(userRepository.existsByEmail(anyString())).thenReturn(true);

            DataIntegrityViolationException exception = Assertions.assertThrows(
                    DataIntegrityViolationException.class,
                    () -> userServiceImpl.validateRegistration("user@gmail.com", "user")
            );

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Email already exists", exception.getMessage());

            verify(userRepository).existsByEmail(anyString());
        }

        @Test
        void validateRegistration_shouldThrow_DataIntegrityValidationException_UsernameExists()
                throws Exception{

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByUsername(anyString())).thenReturn(true);

            DataIntegrityViolationException exception = Assertions.assertThrows(
                    DataIntegrityViolationException.class,
                    () -> userServiceImpl.validateRegistration("user@gmail.com", "user")
            );

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Username already exists", exception.getMessage());

            verify(userRepository).existsByEmail(anyString());
            verify(userRepository).existsByUsername(anyString());
        }

        @Test
        void login_Ok(){
            when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

            User user = userServiceImpl.login(loginRequest);

            Assertions.assertNotNull(user);
            Assertions.assertEquals("user", user.getUsername());

            verify(userRepository).findByUsername(anyString());
        }

        @Test
        void login_shouldThrow_ResourceNotFoundException_UserNotFound() throws Exception{
            when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = Assertions.assertThrows(
                    ResourceNotFoundException.class, () -> userServiceImpl.login(loginRequest)
            );

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Invalid credentials", exception.getMessage());
            verify(userRepository).findByUsername(anyString());
            verifyNoInteractions(passwordEncoder);
        }

        @Test
        void login_shouldThrow_IllegalArgumentException_PasswordDoesntMatch() throws Exception{
            when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            IllegalArgumentException exception = Assertions.assertThrows(
                    IllegalArgumentException.class, () -> userServiceImpl.login(loginRequest)
            );

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Invalid credentials", exception.getMessage());
            verify(userRepository).findByUsername(anyString());
            verify(passwordEncoder).matches(anyString(), anyString());
        }


        @Test
        void registerUser_Ok(){
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            when(userRepository.save(any(User.class))).thenReturn(user);

            User result = userServiceImpl.register(registerRequest);

            Assertions.assertNotNull(result);
            Assertions.assertEquals("user", result.getUsername());
            Assertions.assertEquals("user@gmail.com", result.getEmail());

            verify(userRepository).existsByEmail(anyString());
            verify(userRepository).existsByUsername(anyString());
            verify(passwordEncoder).encode(anyString());
        }

        @Test
        void registerUser_shouldThrow_DataIntegrityValidationException_EmailExists()
                    throws Exception{
            when(userRepository.existsByEmail(anyString())).thenReturn(true);

            DataIntegrityViolationException exception = Assertions.assertThrows(
                    DataIntegrityViolationException.class,
                    () -> userServiceImpl.register(registerRequest)
            );

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Email already exists", exception.getMessage());
            verify(userRepository).existsByEmail(anyString());
        }

        @Test
        void registerUser_shouldThrow_DataIntegrityValidationException_UsernameExists()
                throws Exception{
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByUsername(anyString())).thenReturn(true);

            DataIntegrityViolationException exception = Assertions.assertThrows(
                    DataIntegrityViolationException.class,
                    () -> userServiceImpl.register(registerRequest)
            );

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Username already exists", exception.getMessage());
            verify(userRepository).existsByEmail(anyString());
            verify(userRepository).existsByUsername(anyString());
        }
    }

    @Nested
    @DisplayName("Delete user Tests")
    class DeleteUserTests {

        @Test
        void deleteUserById_Ok(){
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            userServiceImpl.deleteUserById(anyLong());
            verify(userRepository).findById(anyLong());
        }
        
        @Test
        void deleteUserById_shouldThrow_ResourceNotFoundException_UserNotFound()
                    throws Exception{
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = Assertions.assertThrows(
                    ResourceNotFoundException.class, () -> userServiceImpl.deleteUserById(anyLong())
            );

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("User not found", exception.getMessage());
            verify(userRepository).findById(anyLong());
        }

    }

}
