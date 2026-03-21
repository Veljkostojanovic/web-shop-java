package com.webshop.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webshop.JWT.CustomUserDetailsService;
import com.webshop.JWT.JwtService;
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
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({com.fasterxml.jackson.databind.ObjectMapper.class, com.webshop.common.exceptions.GlobalExceptionHandler.class})
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        OrderItemDTO orderItemDTO = new OrderItemDTO(
                1L,
                "Test Product",
                BigDecimal.valueOf(50),
                2,
                BigDecimal.valueOf(100)
        );

        orderDTO = new OrderDTO(
                100L,
                LocalDateTime.now(),
                BigDecimal.valueOf(100),
                List.of(orderItemDTO)
        );
    }

    @Test
    void checkout_ValidRequest_ReturnsOkAndOrderDTO() throws Exception {
        when(orderService.createOrder(anyLong())).thenReturn(orderDTO);

        mockMvc.perform(post("/api/orders/checkout")
                        .param("UserId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(100L))
                .andExpect(jsonPath("$.totalAmount").value(100.00))
                .andExpect(jsonPath("$.items[0].productId").value(1L))
                .andExpect(jsonPath("$.items[0].quantity").value(2));

        verify(orderService, times(1)).createOrder(1L);
    }

    @Test
    void checkout_EmptyCartOrNoStock_ThrowsBadRequest() throws Exception {
        when(orderService.createOrder(anyLong()))
                .thenThrow(new IllegalArgumentException("Cannot checkout with an empty cart!"));

        mockMvc.perform(post("/api/orders/checkout")
                        .param("UserId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot checkout with an empty cart!"));
    }

    @Test
    void getOrder_ExistingOrder_ReturnsOkAndOrderDTO() throws Exception {
        when(orderService.getOrderById(anyLong())).thenReturn(orderDTO);

        mockMvc.perform(get("/api/orders/{orderId}", 100L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(100L))
                .andExpect(jsonPath("$.totalAmount").value(100.00));

        verify(orderService, times(1)).getOrderById(100L);
    }

    @Test
    void getOrder_OrderNotFound_ThrowsResourceNotFoundException() throws Exception {
        when(orderService.getOrderById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Order not found with id: 999"));

        mockMvc.perform(get("/api/orders/{orderId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found with id: 999"));
    }

    @Test
    void getOrdersByUserId_ExistingOrders_ReturnsOkAndList() throws Exception {
        when(orderService.getOrdersByUserId(anyLong())).thenReturn(List.of(orderDTO));

        mockMvc.perform(get("/api/orders/user/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(100L))
                .andExpect(jsonPath("$[0].totalAmount").value(100.00));

        verify(orderService, times(1)).getOrdersByUserId(1L);
    }

    @Test
    void getOrdersByUserId_NoOrders_ReturnsOkAndEmptyList() throws Exception {
        when(orderService.getOrdersByUserId(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/api/orders/user/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(orderService, times(1)).getOrdersByUserId(1L);
    }
}