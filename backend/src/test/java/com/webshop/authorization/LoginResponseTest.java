package com.webshop.authorization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoginResponseTest {
    @Test
    void testLoginResponse() {
        LoginResponse response1 = new LoginResponse();
        response1.setToken("token1");

        LoginResponse response2 = new LoginResponse();
        response2.setToken("token1");

        Assertions.assertEquals("token1", response1.getToken());

        Assertions.assertNotNull(response1.toString());

        Assertions.assertEquals(response1, response2);
        Assertions.assertEquals(response1.hashCode(), response2.hashCode());
    }
}
