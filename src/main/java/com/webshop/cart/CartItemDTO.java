package com.webshop.cart;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long productId;
    private String productName;
    private double productPrice;
    private int quantity;
    private double totalPrice;
}

