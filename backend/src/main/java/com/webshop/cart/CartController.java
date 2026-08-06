package com.webshop.cart;

import com.webshop.JWT.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDTO> getCart(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(cartService.getOrCreateCart(user.getId()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody AddItemRequest request
    ){
        CartDTO updatedCart = cartService.addProductToCart(user.getId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartDTO> updateItemQuantity(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody AddItemRequest request) {

        CartDTO updatedCart = cartService.updateItemQuantity(user.getId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartDTO> removeItemFromCart(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long productId) {

        CartDTO updatedCart = cartService.removeItemFromCart(user.getId(), productId);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserPrincipal user) {
        cartService.clearCart(user.getId());
        return ResponseEntity.noContent().build();
    }
}