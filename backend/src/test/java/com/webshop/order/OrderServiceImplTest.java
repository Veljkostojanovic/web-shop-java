package com.webshop.order;

import com.webshop.cart.Cart;
import com.webshop.cart.CartItem;
import com.webshop.cart.CartRepository;
import com.webshop.common.exceptions.InsufficientStockException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.product.Product;
import com.webshop.product.ProductRepository;
import com.webshop.user.User;
import com.webshop.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;
    private OrderEntity order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        user = new User();

        cart = new Cart();
        cart.setId(1L);
        cart.setUserId(1L);
        cart.setItems(new ArrayList<>());

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(50));
        product.setStock(10);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProductId(1L);
        cartItem.setQuantity(2);

        order = new OrderEntity();
        order.setId(100L);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(BigDecimal.valueOf(100));
        order.setOrderItems(new ArrayList<>());

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(BigDecimal.valueOf(50));
        order.addOrderItem(orderItem);
    }

    @Nested
    @DisplayName("Create Order Tests")
    class CreateOrderTests {
        @Test
        void createOrder_ValidCartAndStock_CreatesOrderClearsCartReturnsDTO() {
            cart.getItems().add(cartItem);
            when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(productRepository.findAllByIdInWithLock(anyList())).thenReturn(List.of(product));
            when(orderRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> {
                OrderEntity saved = invocation.getArgument(0);
                saved.setId(100L);
                return saved;
            });

            OrderResponse result = orderServiceImpl.createOrder(1L);

            assertNotNull(result);
            assertEquals(100L, result.getOrderId());
            assertEquals(BigDecimal.valueOf(100), result.getTotalAmount());
            assertEquals(1, result.getItems().size());
            assertEquals(8, product.getStock());
            assertTrue(cart.getItems().isEmpty());

            verify(orderRepository, times(1)).save(any(OrderEntity.class));
            verify(cartRepository, times(1)).save(cart);
        }

        @Test
        void createOrder_CartNotFound_ThrowsException() {
            when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> orderServiceImpl.createOrder(1L));

            assertEquals("No cart found for user: 1", exception.getMessage());

            verify(orderRepository, never()).save(any(OrderEntity.class));
        }

        @Test
        void createOrder_EmptyCart_ThrowsException() {
            when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> orderServiceImpl.createOrder(1L));

            assertEquals("Cannot checkout with an empty cart!", exception.getMessage());
            verify(orderRepository, never()).save(any(OrderEntity.class));
        }

        @Test
        void createOrder_UserNotFound_ThrowsException() {
            cart.getItems().add(cartItem);
            when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> orderServiceImpl.createOrder(1L));

            assertEquals("User not found", exception.getMessage());
            verify(orderRepository, never()).save(any(OrderEntity.class));
        }

        @Test
        void createOrder_NotEnoughStock_ThrowsException() {
            cart.getItems().add(cartItem);
            product.setStock(1);

            when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(productRepository.findAllByIdInWithLock(anyList())).thenReturn(List.of(product));

            InsufficientStockException exception = assertThrows(InsufficientStockException.class,
                    () -> orderServiceImpl.createOrder(1L));

            assertEquals("Not enough stock for: Test Product", exception.getMessage());

            verify(orderRepository, never()).save(any(OrderEntity.class));
        }
    }

    @Nested
    @DisplayName("Get Order Tests")
    class GetOrderTests {

        @Test
        void getOrderById_ExistingOrder_ReturnsOrderDTO() {
            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

            OrderResponse result = orderServiceImpl.getOrderById(100L);

            assertNotNull(result);
            assertEquals(100L, result.getOrderId());
            assertEquals(BigDecimal.valueOf(100), result.getTotalAmount());
            assertEquals(1, result.getItems().size());
            assertEquals("Test Product", result.getItems().get(0).getProductName());

            verify(orderRepository, times(1)).findById(anyLong());
        }

        @Test
        void getOrderById_OrderNotFound_ThrowsException() {
            when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> orderServiceImpl.getOrderById(100L));

            assertEquals("Order not found with id: 100", exception.getMessage());

            verify(orderRepository, times(1)).findById(anyLong());
        }

        @Test
        void getOrdersByUserId_ExistingOrders_ReturnsDTOList() {
            Page<OrderEntity> page = new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1);
            OrderFilterRequest filterRequest = new OrderFilterRequest();

            when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            var results = orderServiceImpl.getOrdersByUserId(1L, filterRequest, PageRequest.of(0, 10));

            assertNotNull(results);
            assertFalse(results.isEmpty());
            assertEquals(1, results.getTotalElements());
            assertEquals(100L, results.getContent().get(0).getOrderId());

            verify(orderRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        void getOrdersByUserId_NoOrders_ReturnsEmptyList() {
            Page<OrderEntity> page = new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 10), 0);
            OrderFilterRequest filterRequest = new OrderFilterRequest();

            when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            var results = orderServiceImpl.getOrdersByUserId(1L, filterRequest, PageRequest.of(0, 10));

            assertNotNull(results);
            assertTrue(results.isEmpty());

            verify(orderRepository, times(1)).findAll(any(org.springframework.data.jpa.domain.Specification.class), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Paging Tests")
    class PagingTests {

        @Test
        void getOrdersByUserId_withValidPageable_returnsPage() {
            Page<OrderEntity> page = new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1);
            OrderFilterRequest filterRequest = new OrderFilterRequest();

            when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            var result = orderServiceImpl.getOrdersByUserId(1L, filterRequest, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "orderDate")));

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getContent().size());
        }

        @Test
        void getOrdersByUserId_withInvalidSort_throwsBadRequest() {
            OrderFilterRequest filterRequest = new OrderFilterRequest();

            assertThrows(com.webshop.common.exceptions.BadRequestException.class, () ->
                    orderServiceImpl.getOrdersByUserId(1L, filterRequest, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "invalidField")))
            );
        }
    }
}