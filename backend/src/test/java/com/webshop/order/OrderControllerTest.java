package com.webshop.order;

import com.webshop.JWT.JwtAuthFilter;
import com.webshop.JWT.JwtService;
import com.webshop.JWT.UserPrincipal;
import com.webshop.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private UserPrincipal userPrincipal;
    private OrderResponse sampleOrderResponse;
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        userPrincipal = new UserPrincipal(USER_ID, "testuser@example.com", "username", "password", Collections.emptyList());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        sampleOrderResponse = new OrderResponse();
    }

    @Nested
    @DisplayName("POST /api/orders/checkout")
    class CheckoutTests {

        @Test
        void checkout_Success_Returns201() throws Exception {
            when(orderService.createOrder(USER_ID)).thenReturn(sampleOrderResponse);

            mockMvc.perform(post("/api/orders/checkout")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());

            verify(orderService).createOrder(USER_ID);
        }

        @Test
        void checkout_ServiceThrowsException_PropagatesError() throws Exception {
            when(orderService.createOrder(USER_ID))
                    .thenThrow(new RuntimeException("Cart is empty"));

            mockMvc.perform(post("/api/orders/checkout")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result ->
                            Assertions.assertTrue(
                                    result.getResolvedException() instanceof RuntimeException
                            )
                    );

            verify(orderService).createOrder(USER_ID);
        }
    }

    @Nested
    @DisplayName("GET /api/orders/{orderId}")
    class GetOrderTests {

        @Test
        void getOrder_Success_Returns200() throws Exception {
            Long orderId = 100L;
            when(orderService.getOrder(orderId, USER_ID)).thenReturn(sampleOrderResponse);

            mockMvc.perform(get("/api/orders/{orderId}", orderId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(orderService).getOrder(orderId, USER_ID);
        }

        @Test
        void getOrder_InvalidOrderIdType_Returns400() throws Exception {
            mockMvc.perform(get("/api/orders/{orderId}", "invalid-id")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void getOrder_NotFound_PropagatesException() throws Exception {
            Long orderId = 999L;
            when(orderService.getOrder(orderId, USER_ID))
                    .thenThrow(new ResourceNotFoundException("Order not found"));

            mockMvc.perform(get("/api/orders/{orderId}", orderId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result ->
                            Assertions.assertTrue(
                                    result.getResolvedException() instanceof ResourceNotFoundException
                            )
                    );

            verify(orderService).getOrder(orderId, USER_ID);
        }
    }

    @Nested
    @DisplayName("GET /api/orders/user")
    class GetOrdersByUserIdTests {

        @Test
        void getOrdersByUserId_DefaultParams_ReturnsPagedOrders() throws Exception {
            Page<OrderResponse> page = new PageImpl<>(List.of(sampleOrderResponse));
            when(orderService.getOrdersByUserId(eq(USER_ID), any(OrderFilterRequest.class), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/orders/user")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(1));

            verify(orderService).getOrdersByUserId(eq(USER_ID), any(OrderFilterRequest.class), any(Pageable.class));
        }

        @Test
        void getOrdersByUserId_WithPaginationQueryParams_PassesPageableToService() throws Exception {
            Page<OrderResponse> page = new PageImpl<>(List.of(sampleOrderResponse), PageRequest.of(1, 5), 10);

            when(orderService.getOrdersByUserId(eq(USER_ID), any(OrderFilterRequest.class), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/orders/user")
                            .param("page", "1")
                            .param("size", "5")
                            .param("sort", "createdAt,desc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.number").value(1))
                    .andExpect(jsonPath("$.size").value(5))
                    .andExpect(jsonPath("$.totalElements").value(10));

            verify(orderService).getOrdersByUserId(eq(USER_ID), any(OrderFilterRequest.class), any(Pageable.class));
        }

        @Test
        void getOrdersByUserId_EmptyResults_ReturnsEmptyPage() throws Exception {
            Page<OrderResponse> emptyPage = new PageImpl<>(Collections.emptyList());
            when(orderService.getOrdersByUserId(eq(USER_ID), any(OrderFilterRequest.class), any(Pageable.class)))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/api/orders/user")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(orderService).getOrdersByUserId(eq(USER_ID), any(OrderFilterRequest.class), any(Pageable.class));
        }

        @Test
        void getOrdersByUserId_WithFilterParams_ExecutesSuccessfully() throws Exception {
            Page<OrderResponse> page = new PageImpl<>(List.of(sampleOrderResponse));
            when(orderService.getOrdersByUserId(eq(USER_ID), any(OrderFilterRequest.class), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/orders/user")
                            .param("status", "DELIVERED")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(orderService).getOrdersByUserId(eq(USER_ID), any(OrderFilterRequest.class), any(Pageable.class));
        }
    }
}