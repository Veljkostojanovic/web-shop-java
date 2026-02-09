package com.webshop.cart;

import com.webshop.cartItem.CartItem;
import com.webshop.cartItem.CartItemDTO;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public class CartMapper {

    public static CartDTO toDTO(Cart cart) {
        if(cart == null) return null;
        CartDTO dto = new CartDTO();
        dto.setItems(cart.getItems().stream()
                .map(CartMapper::toDTO)
                .collect(Collectors.toList()));
        dto.setTotalPrice(cart.getItems().stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return dto;
    }

    public static CartItemDTO toDTO(CartItem item) {
        return new CartItemDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
        );
    }
}