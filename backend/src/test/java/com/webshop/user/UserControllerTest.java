package com.webshop.user;

import com.webshop.JWT.CustomUserDetailsService;
import com.webshop.JWT.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({com.fasterxml.jackson.databind.ObjectMapper.class, com.webshop.common.exceptions.GlobalExceptionHandler.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void getCurrentUser_ShouldReturnUser() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "user123", "user123@gmail.com", "USER");

        when(userService.findUserByUsername(anyString())).thenReturn(userDTO);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("user123", null, new ArrayList<>());

        mockMvc.perform(get("/api/users/me").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user123"))
                .andExpect(jsonPath("$.email").value("user123@gmail.com"));

        verify(userService).findUserByUsername(anyString());
    }

}
