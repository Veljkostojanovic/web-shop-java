package com.webshop.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserMapperTest {

    @Test
    void toDTO_shouldReturnNull() {
        UserDTO result =  UserMapper.toDTO(null);
        Assertions.assertNull(result);
    }

    @Test
    void toDTO_shouldReturnUser() {
        User user = new User(1L, "User", "user@gmail.com", "Password", "USER");

        UserDTO result = UserMapper.toDTO(user);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), user.getId());
        Assertions.assertEquals(result.getEmail(), user.getEmail());
        Assertions.assertEquals(result.getRole(), user.getRole());
        Assertions.assertEquals(result.getUsername(), user.getUsername());
    }
}
