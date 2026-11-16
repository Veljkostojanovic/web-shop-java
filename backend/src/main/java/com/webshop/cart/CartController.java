package com.webshop.cart;

import com.webshop.JWT.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    @GetMapping
    public CartResponse getCart(@AuthenticationPrincipal UserPrincipal user) {
        return cartService.getCart(user.getId());
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid AddCartItemRequest request){
        CartResponse updatedCart = cartService.addProductToCart(user.getId(), request.productId(), request.quantity());
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long productId,
            @RequestBody @Valid UpdateCartItemRequest request) {
        CartResponse updatedCart = cartService.updateItemQuantity(user.getId(), productId, request.quantity());
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItemFromCart(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long productId) {
        CartResponse updatedCart = cartService.removeItemFromCart(user.getId(), productId);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@AuthenticationPrincipal UserPrincipal user) {
        cartService.clearCart(user.getId());
    }
}