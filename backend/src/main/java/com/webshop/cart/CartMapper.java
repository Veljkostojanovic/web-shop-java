package com.webshop.cart;

import com.webshop.product.Product;

import java.math.BigDecimal;
import java.util.List;

public final class CartMapper {

    private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO.setScale(Product.PRICE_SCALE);

    private CartMapper() {
    }

    public static CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(CartMapper::toItemResponse)
                .toList();

        BigDecimal total = cart.getItems().stream()
                .filter(CartItem::isPurchasable)
                .map(CartItem::lineTotal)
                .reduce(ZERO_AMOUNT, BigDecimal::add);

        int totalQuantity = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        boolean checkoutReady = !items.isEmpty() && items.stream().allMatch(CartItemResponse::available);

        return new CartResponse(items, totalQuantity, total, checkoutReady);
    }

    public static CartResponse emptyResponse() {
        return new CartResponse(List.of(), 0, ZERO_AMOUNT, false);
    }

    private static CartItemResponse toItemResponse(CartItem item) {
        Product product = item.getProduct();
        return new CartItemResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                item.getQuantity(),
                item.lineTotal(),
                item.isPurchasable(),
                product.isActive() ? product.getStock() : 0);
    }
}