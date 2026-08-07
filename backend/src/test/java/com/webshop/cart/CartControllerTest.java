package com.webshop.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webshop.JWT.CustomUserDetailsService;
import com.webshop.JWT.JwtService;
import com.webshop.JWT.UserPrincipal;
import com.webshop.cartItem.CartItemDTO;
import com.webshop.common.exceptions.GlobalExceptionHandler;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ObjectMapper.class, GlobalExceptionHandler.class})
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    private CartDTO cart;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        CartItemDTO cartItemDTO = new CartItemDTO(1L, "Product", BigDecimal.valueOf(10), 2, BigDecimal.valueOf(20));
        cart = new CartDTO(List.of(cartItemDTO), BigDecimal.valueOf(20));
        
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRole("USER");

        userPrincipal = new UserPrincipal(user);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getCart_ReturnsOkandCartDTO() throws Exception {
        when(cartService.getOrCreateCart(1L)).thenReturn(cart);

        mockMvc.perform(get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(20.00))
                .andExpect(jsonPath("$.items[0].productId").value(1L))
                .andExpect(jsonPath("$.items[0].quantity").value(2));

        verify(cartService, times(1)).getOrCreateCart(1L);
    }

    @Test
    void addProductToCart_ValidRequest_ReturnsOkandUpdateCart() throws Exception {
        AddItemRequest request = new AddItemRequest(1L, 2);

        when(cartService.addProductToCart(1L, 1L, 2)).thenReturn(cart);

        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(20.00))
                .andExpect(jsonPath("$.items[0].productId").value(1L));

        verify(cartService, times(1)).addProductToCart(1L, 1L, 2);
    }

    @Test
    void addProductToCart_ProductNotFound_ThrowsResourceNotFoundException() throws Exception {
        AddItemRequest request = new AddItemRequest(90L, 2);

        when(cartService.addProductToCart(1L, 90L, 2))
                .thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void addProductToCart_BadRequest_ThrowsBadRequest() throws Exception {
        AddItemRequest request = new AddItemRequest(1L, -1);

        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItemQuantity_ValidRequest_ReturnsOkAndUpdatedCart() throws Exception {
        AddItemRequest request = new AddItemRequest(1L, 5);
        
        when(cartService.updateItemQuantity(1L, 1L, 5)).thenReturn(cart);

        mockMvc.perform(put("/api/cart/items/{productId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(20.00));

        verify(cartService, times(1)).updateItemQuantity(1L, 1L, 5);
    }

    @Test
    void updateItemQuantity_ProductNotFound_ThrowsResourceNotFound() throws Exception {
        AddItemRequest request = new AddItemRequest(1L, 5);
        
        when(cartService.updateItemQuantity(1L, 1L, 5))
                .thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(put("/api/cart/items/{productId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void removeItemFromCart_ValidRequest_ReturnsOkAndUpdatedCart() throws Exception {
        when(cartService.removeItemFromCart(1L, 1L)).thenReturn(cart);

        mockMvc.perform(delete("/api/cart/items/{productId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(20.00));

        verify(cartService, times(1)).removeItemFromCart(1L, 1L);
    }

    @Test
    void clearCart_ValidRequest_ReturnsNoContent() throws Exception {
        doNothing().when(cartService).clearCart(1L);

        mockMvc.perform(delete("/api/cart"))
                .andExpect(status().isNoContent());

        verify(cartService, times(1)).clearCart(1L);
    }
}