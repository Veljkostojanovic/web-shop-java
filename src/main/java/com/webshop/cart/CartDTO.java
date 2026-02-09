package com.webshop.cart;

import com.webshop.cartItem.CartItemDTO;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private List<CartItemDTO> items;
    private BigDecimal totalPrice;
}

