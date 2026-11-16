package com.webshop.cart;

import com.webshop.product.ProductResponse;
import java.math.BigDecimal;
import java.util.List;

public class CartMapper {

    public static CartResponse toDTO(Cart cart, List<CartItemResponse> itemDTOs) {
        if (cart == null) return null;

        BigDecimal total = itemDTOs.stream()
                .map(CartItemResponse::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(itemDTOs, total);
    }

    public static CartItemResponse toCartItemDTO(CartItem item, ProductResponse product) {
        BigDecimal totalPrice = product.price().multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponse(
                product.id(),
                product.name(),
                product.price(),
                item.getQuantity(),
                totalPrice
        );
    }
}