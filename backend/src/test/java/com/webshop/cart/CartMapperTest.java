package com.webshop.cart;

import com.webshop.cartItem.CartItem;
import com.webshop.cartItem.CartItemDTO;
import com.webshop.product.ProductDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;

public class CartMapperTest {

    @Test
    void toDto_NullDto() throws Exception {
        CartDTO cartDTO = CartMapper.toDTO(null, null);
        Assertions.assertNull(cartDTO);
    }

    @Test
    void toDto_Ok() throws Exception {
        CartItem cartItem = new CartItem();
        cartItem.setProductId(1L);
        cartItem.setQuantity(1);

        CartItemDTO cartItemDTO = new  CartItemDTO();
        cartItemDTO.setProductId(1L);
        cartItemDTO.setQuantity(1);
        cartItemDTO.setTotalPrice(BigDecimal.valueOf(500));

        List<CartItem> cartItems = List.of(cartItem);
        List<CartItemDTO> cartItemDTOs = List.of(cartItemDTO);
        Cart cart = new Cart(1L, 1L, cartItems);

        CartDTO result = CartMapper.toDTO(cart, cartItemDTOs);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getItems().size());
        Assertions.assertEquals(1, result.getItems().getFirst().getProductId());
        Assertions.assertEquals(1, result.getItems().getFirst().getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(500), cartItemDTOs.getFirst().getTotalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(500), result.getTotalPrice());
    }

    @Test
    void toCartItemDto_Ok() throws Exception {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(1);
        ProductDTO productDTO = new ProductDTO(1L, "Product", null, BigDecimal.valueOf(500), 0, null);

        CartItemDTO result = CartMapper.toCartItemDTO(cartItem, productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getProductId());
        Assertions.assertEquals("Product", result.getProductName());
        Assertions.assertEquals(1, result.getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(500), result.getTotalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(500), result.getProductPrice());
    }
}
