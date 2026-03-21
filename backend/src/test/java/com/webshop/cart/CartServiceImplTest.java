package com.webshop.cart;

import com.webshop.cartItem.CartItem;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.product.Product;
import com.webshop.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartServiceImpl cartServiceImpl;


    private Product product;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setId(1L);
        cart.setUserId(1L);
        cart.setItems(new ArrayList<>());

        product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setPrice(BigDecimal.valueOf(10));

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProductId(1L);
        cartItem.setQuantity(2);
    }

    @Nested
    @DisplayName("Create cart tests")
    class CreateCartTests{

        @Test
        void getOrCreateCart_ExistingCart_ReturnsDTO() {
            cart.getItems().add(cartItem);

            when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
            when(productRepository.findAllById(anyList())).thenReturn(List.of(product));

            CartDTO result = cartServiceImpl.getOrCreateCart(1L);

            assertNotNull(result);
            assertEquals(1, result.getItems().size());
            assertEquals(BigDecimal.valueOf(20), result.getTotalPrice());

            verify(cartRepository, never()).save(any(Cart.class));
        }

        @Test
        void getOrCreateCart_NewCart_CreatesAndReturnsEmptyCart() {
            when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.empty());
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            CartDTO result = cartServiceImpl.getOrCreateCart(1L);

            assertNotNull(result);
            assertTrue(result.getItems().isEmpty());
            assertEquals(BigDecimal.valueOf(0), result.getTotalPrice());
            verify(cartRepository, times(1)).save(any(Cart.class));
        }
    }

    @Nested
    @DisplayName("Add product to cart tests")
    class AddProductToCartTests{

        @Test
        void addProductToCart_NewProduct_AddsItemAndReturnsDTO() {
            when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
            when(productRepository.findAllById(anyList())).thenReturn(List.of(product));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            CartDTO result = cartServiceImpl.addProductToCart(1L, 1L, 3);

            assertNotNull(result);
            assertEquals(1L, result.getItems().size());
            assertEquals(3, result.getItems().getFirst().getQuantity());
            assertEquals(BigDecimal.valueOf(30), result.getTotalPrice());

            verify(cartRepository, times(1)).save(any(Cart.class));
        }

        @Test
        void addProductToCart_ExistingProduct_IncreasesQuantityReturnsDTO() {
            cart.getItems().add(cartItem);

            when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
            when(productRepository.findAllById(anyList())).thenReturn(List.of(product));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            CartDTO result = cartServiceImpl.addProductToCart(1L, 1L, 3);

            assertNotNull(result);
            assertEquals(1L, result.getItems().size());
            assertEquals(5, result.getItems().getFirst().getQuantity());
            assertEquals(BigDecimal.valueOf(50), result.getTotalPrice());

            verify(cartRepository, times(1)).save(any(Cart.class));
        }

        @Test
        void addProductToCart_ProductNotFound_ThrowsException(){
            when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            ResourceNotFoundException exception =  assertThrows(ResourceNotFoundException.class,
                    () -> cartServiceImpl.addProductToCart(1L, 1L, 3));

            assertNotNull(exception);
            assertEquals("Product not found with id: 1", exception.getMessage());

            verify(cartRepository, never()).save(any(Cart.class));
        }
    }

    @Nested
    @DisplayName("Update item quantity tests")
    class UpdateItemQuantityTests {

        @Test
        void updateItemQuantity_QuantityPositive_updatesQuantity() {
            cart.getItems().add(cartItem);

            when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
            when(productRepository.findAllById(anyList())).thenReturn(List.of(product));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            CartDTO result =  cartServiceImpl.updateItemQuantity(1L, 1L, 5);

            assertNotNull(result);
            assertEquals(1, result.getItems().size());
            assertEquals(5, result.getItems().getFirst().getQuantity());
            assertEquals(BigDecimal.valueOf(50), result.getTotalPrice());
        }

        @Test
        void updateItemQuantity_QuantityLessThanOrEqual0_RemovesItem() {
            cart.getItems().add(cartItem);

            when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
            when(productRepository.findAllById(anyList())).thenReturn(List.of(product));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            CartDTO result =  cartServiceImpl.updateItemQuantity(1L, 1L, 0);

            assertNotNull(result);
            assertTrue(result.getItems().isEmpty());
            assertEquals(BigDecimal.ZERO, result.getTotalPrice());
        }
    }


    @Nested
    @DisplayName("Remove Item and clear cart tests")
    class RemoveItemAndClearCartTests {
        @Test
        void removeItemFromCart_ExistingItem_RemovesItem() {
            cart.getItems().add(cartItem);
            when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));
            when(productRepository.findAllById(anyList())).thenReturn(List.of(product));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            CartDTO result = cartServiceImpl.removeItemFromCart(1L, 1L);

            assertNotNull(result);
            assertTrue(result.getItems().isEmpty());
            assertEquals(BigDecimal.ZERO, result.getTotalPrice());

            verify(cartRepository, times(1)).save(any(Cart.class));
        }

        @Test
        void clearCart_ExistingCart_ClearsCartItems() {
            cart.getItems().add(cartItem);
            when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));

            cartServiceImpl.clearCart(1L);

            assertTrue(cart.getItems().isEmpty());

            verify(cartRepository, times(1)).save(any(Cart.class));
        }
    }
}