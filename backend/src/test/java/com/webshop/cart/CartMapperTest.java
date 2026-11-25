package com.webshop.cart;

import com.webshop.product.ProductResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

public class CartMapperTest {

    @Test
    void toDto_NullDto() throws Exception {
        CartResponse cartResponse = CartMapper.toDTO(null, null);
        Assertions.assertNull(cartResponse);
    }

    @Test
    void toDto_Ok() throws Exception {
        CartItem cartItem = new CartItem();
        cartItem.setProductId(1L);
        cartItem.setQuantity(1);

        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setProductId(1L);
        cartItemResponse.setQuantity(1);
        cartItemResponse.setTotalPrice(BigDecimal.valueOf(500));

        List<CartItem> cartItems = List.of(cartItem);
        List<CartItemResponse> cartItemResponses = List.of(cartItemResponse);
        Cart cart = new Cart(1L, 1L, cartItems);

        CartResponse result = CartMapper.toDTO(cart, cartItemResponses);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getItems().size());
        Assertions.assertEquals(1, result.getItems().getFirst().getProductId());
        Assertions.assertEquals(1, result.getItems().getFirst().getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(500), cartItemResponses.getFirst().getTotalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(500), result.getTotalPrice());
    }

    @Test
    void toCartItemDto_Ok() throws Exception {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(1);
        ProductResponse productResponse = new ProductResponse(1L, "Product", null, BigDecimal.valueOf(500), 0, null);

        CartItemResponse result = CartMapper.toCartItemDTO(cartItem, productResponse);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getProductId());
        Assertions.assertEquals("Product", result.getProductName());
        Assertions.assertEquals(1, result.getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(500), result.getTotalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(500), result.getProductPrice());

    }
}
