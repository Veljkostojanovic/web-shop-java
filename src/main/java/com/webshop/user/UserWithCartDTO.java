package com.webshop.user;

import com.webshop.cartItem.CartItemDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserWithCartDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private List<CartItemDTO> cartItems;
}
