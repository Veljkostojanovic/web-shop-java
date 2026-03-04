package com.webshop.admin;

import com.webshop.JWT.CustomUserDetailsService;
import com.webshop.JWT.JwtService;
import com.webshop.user.UserDTO;
import com.webshop.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ShouldReturnUserList() throws Exception {
        UserDTO user1 = new UserDTO();
        user1.setUsername("user");
        user1.setEmail("user@gmail.com");

        when(userService.getAllUsers()).thenReturn(List.of(user1));

        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].username").value("user"))
                .andExpect(jsonPath("$[0].email").value("user@gmail.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_ShouldCallServiceAndReturn200() throws Exception {
        mockMvc.perform(delete("/api/admin/users/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userService).deleteUserById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserByUsername_ShouldReturnUser() throws Exception {
        UserDTO user = new UserDTO();
        user.setUsername("user");
        when(userService.findUserByUsername(anyString())).thenReturn(user);

        mockMvc.perform(get("/api/admin/username/john_doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserByEmail_ShouldReturnUser() throws Exception {
        UserDTO user = new UserDTO();
        user.setEmail("user@gmail.com");
        when(userService.findUserByEmail(anyString())).thenReturn(user);

        mockMvc.perform(get("/api/admin/email/user@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@gmail.com"));
    }
}
