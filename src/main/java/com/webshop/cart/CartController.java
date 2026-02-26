package com.webshop.cart;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class CartController {
    /*
        Cart getCartByUserId(Long userId);
        void addItemToCart(Long userId, Long productId, int quantity);
        void updateItemQuantity(Long userId, Long productId, int quantity);
        void removeItemFromCart(Long userId, Long productId);
        void clearCart(Long userId);
     */

    private final CartService cartService;

    @GetMapping("/getCart/{id}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long id){
        CartDTO cartDTO = cartService.getCartByUserId(id);
        return ResponseEntity.ok(cartDTO);
    }

    @PostMapping ("{userId}")// doesnt work
    public ResponseEntity<Void> addItemToCart(@PathVariable Long userId, @Valid @RequestBody AddToCartRequest request){
        cartService.addItemToCart(userId,
                request.productId(),
                request.quantity());
        return ResponseEntity.ok().build();
    }

    @PutMapping("{userId}")
    public ResponseEntity<Void> updateItemItemQuantity(@PathVariable Long userId, @Valid @RequestBody AddToCartRequest request){
        cartService.updateItemQuantity(userId, request.productId(), request.quantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{userId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long userId, @RequestParam Long productId) {
        cartService.removeItemFromCart(userId, productId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return  ResponseEntity.noContent().build();
    }
}
