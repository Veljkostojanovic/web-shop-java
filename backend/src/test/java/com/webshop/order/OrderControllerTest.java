package com.webshop.order;

import com.webshop.JWT.CustomUserDetailsService;
import com.webshop.JWT.JwtService;
import com.webshop.JWT.UserPrincipal;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
    private UserPrincipal userPrincipal;

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

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRole("USER");
        
        userPrincipal = new UserPrincipal(user);
    }

    @Test
    void checkout_ValidRequest_ReturnsCreatedAndOrderDTO() throws Exception {
        when(orderService.createOrder(1L)).thenReturn(orderDTO);

        mockMvc.perform(post("/api/orders/checkout")
                        .with(user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(100L))
                .andExpect(jsonPath("$.totalAmount").value(100.00))
                .andExpect(jsonPath("$.items[0].productId").value(1L))
                .andExpect(jsonPath("$.items[0].quantity").value(2));

        verify(orderService, times(1)).createOrder(1L);
    }

    @Test
    void getOrder_ExistingOrder_ReturnsOkAndOrderDTO() throws Exception {
        when(orderService.getOrder(100L, 1L)).thenReturn(orderDTO);

        mockMvc.perform(get("/api/orders/{orderId}", 100L)
                        .with(user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(100L))
                .andExpect(jsonPath("$.totalAmount").value(100.00));

        verify(orderService, times(1)).getOrder(100L, 1L);
    }

    @Test
    void getOrder_OrderNotFound_ThrowsResourceNotFoundException() throws Exception {
        when(orderService.getOrder(999L, 1L))
                .thenThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(get("/api/orders/{orderId}", 999L)
                        .with(user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found"));
    }

    @Test
    void getOrdersByUserId_ExistingOrders_ReturnsOkAndPage() throws Exception {
        org.springframework.data.domain.Page<OrderDTO> page = new org.springframework.data.domain.PageImpl<>(List.of(orderDTO));

        when(orderService.getOrdersByUserId(eq(1L), any(OrderFilterRequest.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/orders/user")
                        .with(user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderId").value(100L))
                .andExpect(jsonPath("$.content[0].totalAmount").value(100.00));

        verify(orderService, times(1)).getOrdersByUserId(eq(1L), any(OrderFilterRequest.class), any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void getOrdersByUserId_NoOrders_ReturnsOkAndEmptyPage() throws Exception {
        org.springframework.data.domain.Page<OrderDTO> page = new org.springframework.data.domain.PageImpl<>(List.of());

        when(orderService.getOrdersByUserId(eq(1L), any(OrderFilterRequest.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/orders/user")
                        .with(user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        verify(orderService, times(1)).getOrdersByUserId(eq(1L), any(OrderFilterRequest.class), any(org.springframework.data.domain.Pageable.class));
    }
}