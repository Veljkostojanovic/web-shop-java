package com.webshop.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webshop.JWT.CustomUserDetailsService;
import com.webshop.JWT.JwtService;
import com.webshop.cartItem.CartItemDTO;
import com.webshop.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({com.fasterxml.jackson.databind.ObjectMapper.class, com.webshop.common.exceptions.GlobalExceptionHandler.class})
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

    @BeforeEach
    void setUp() {
        CartItemDTO cartItemDTO = new CartItemDTO(1L, "Product", BigDecimal.valueOf(10), 2, BigDecimal.valueOf(20));
        cart = new CartDTO(List.of(cartItemDTO), BigDecimal.valueOf(20));
    }



    @Test
    void getCart_ReturnsOkandCartDTO() throws Exception {
        when(cartService.getOrCreateCart(anyLong())).thenReturn(cart);

        mockMvc.perform(get("/api/carts/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(20.00))
                .andExpect(jsonPath("$.items[0].productId").value(1L))
                .andExpect(jsonPath("$.items[0].quantity").value(2));

        verify(cartService, times(1)).getOrCreateCart(anyLong());
    }

    @Test
    void addProductToCart_ValidRequest_ReturnsOkandUpdateCart() throws Exception {
        AddItemRequest request = new AddItemRequest(1L, 2);

        when(cartService.addProductToCart(anyLong(), anyLong(), anyInt())).thenReturn(cart);

        mockMvc.perform(post("/api/carts/{userId}/items", 1L)
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

        when(cartService.addProductToCart(anyLong(), anyLong(), anyInt()))
                .thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(post("/api/carts/{userId}/items", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void addProductToCart_BadRequest_ThrowsBadRequest() throws Exception {
        AddItemRequest request = new AddItemRequest(1L, -1);

        mockMvc.perform(post("/api/carts/{userId}/items", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateItemQuantity_ValidRequest_ReturnsOkAndUpdatedCart() throws Exception {
        when(cartService.updateItemQuantity(anyLong(), anyLong(), anyInt())).thenReturn(cart);


        mockMvc.perform(put("/api/carts/{userId}/items/{productId}", 1L, 1L)
                        .param("quantity", String.valueOf(5))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(20.00));

        verify(cartService, times(1)).updateItemQuantity(1L, 1L, 5);
    }

    @Test
    void updateItemQuantity_ProductNotFound_ThrowsResourceNotFound() throws Exception {
        when(cartService.updateItemQuantity(anyLong(), anyLong(), anyInt()))
                .thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(put("/api/carts/{userId}/items/{productId}", 1L, 1L)
                        .param("quantity", String.valueOf(5))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void removeItemFromCart_ValidRequest_ReturnsOkAndUpdatedCart() throws Exception {
        when(cartService.removeItemFromCart(anyLong(), anyLong())).thenReturn(cart);

        mockMvc.perform(delete("/api/carts/{userId}/items/{productId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(20.00));

        verify(cartService, times(1)).removeItemFromCart(1L, 1L);
    }

    @Test
    void clearCart_ValidRequest_ReturnsNoContent() throws Exception {
        doNothing().when(cartService).clearCart(1L);

        mockMvc.perform(delete("/api/carts/{userId}", 1L))
                .andExpect(status().isNoContent());

        verify(cartService, times(1)).clearCart(1L);
    }
}