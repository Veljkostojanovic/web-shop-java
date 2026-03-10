package com.webshop.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webshop.JWT.CustomUserDetailsService;
import com.webshop.JWT.JwtService;
import com.webshop.user.User;
import org.springframework.http.MediaType;
import com.webshop.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({com.fasterxml.jackson.databind.ObjectMapper.class, com.webshop.common.exceptions.GlobalExceptionHandler.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserServiceImpl userServiceImpl;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        mockUser.setEmail("test@gmail.com");
        mockUser.setRole("USER");
    }

    @Test
    void login_ShouldReturnToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testUser", "password");
        String mockToken = "mocked-jwt-token";

        when(userServiceImpl.login(any(LoginRequest.class))).thenReturn(mockUser);
        when(jwtService.generateToken(mockUser)).thenReturn(mockToken);


        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(mockToken));

        verify(userServiceImpl).login(any(LoginRequest.class));
        verify(jwtService).generateToken(mockUser);
    }

    @Test
    void register_ShouldReturnUserResponse() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "test@gmail.com", "password");

        when(userServiceImpl.register(any(RegisterRequest.class))).thenReturn(mockUser);

        mockMvc.perform(post("/api/auth/register")
                        .content(objectMapper.writeValueAsString(registerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.id").value(1));

        verify(userServiceImpl).register(any(RegisterRequest.class));
    }
}
