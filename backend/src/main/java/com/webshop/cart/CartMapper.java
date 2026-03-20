package com.webshop.cart;

import com.webshop.cartItem.CartItem;
import com.webshop.cartItem.CartItemDTO;
import com.webshop.product.ProductDTO;
import java.math.BigDecimal;
import java.util.List;

public class CartMapper {

    public static CartDTO toDTO(Cart cart, List<CartItemDTO> itemDTOs) {
        if (cart == null) return null;

        BigDecimal total = itemDTOs.stream()
                .map(CartItemDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartDTO(itemDTOs, total);
    }

    public static CartItemDTO toCartItemDTO(CartItem item, ProductDTO product) {
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                item.getQuantity(),
                totalPrice
        );
    }
}