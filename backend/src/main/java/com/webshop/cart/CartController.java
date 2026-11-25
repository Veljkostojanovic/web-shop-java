package com.webshop.cart;

import com.webshop.JWT.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(cartService.getCart(user.getId()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(@AuthenticationPrincipal UserPrincipal user,
                                                      @RequestBody @Valid AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addProductToCart(user.getId(), request.productId(), request.quantity()));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateItemQuantity(@AuthenticationPrincipal UserPrincipal user,
                                                           @PathVariable @Positive Long productId,
                                                           @RequestBody @Valid UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(user.getId(), productId, request.quantity()));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItemFromCart(@AuthenticationPrincipal UserPrincipal user,
                                                           @PathVariable @Positive Long productId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(user.getId(), productId));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserPrincipal user) {
        cartService.clearCart(user.getId());
        return ResponseEntity.noContent().build();
    }
}