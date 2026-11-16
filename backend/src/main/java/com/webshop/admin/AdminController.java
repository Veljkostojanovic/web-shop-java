package com.webshop.admin;

import com.webshop.order.OrderResponse;
import com.webshop.order.OrderFilter;
import com.webshop.order.OrderService;
import com.webshop.user.UserResponse;
import com.webshop.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/users")
    public Page<UserResponse> getAllUsers(
            @ParameterObject Pageable pageable
    ) {
        return userService.getAllUsers(pageable);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable @Positive Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/by-username")
    public ResponseEntity<UserResponse> getUserByUsername (@RequestParam @NotBlank @Size(max = 255) String username){
        return ResponseEntity.ok(userService.findUserByUsername(username));
    }

    @GetMapping("/users/by-email")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam @NotBlank @Email @Size(max = 255) String email){
        return ResponseEntity.ok(userService.findUserByEmail(email));
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @ParameterObject @Valid OrderFilter filter,
            @ParameterObject Pageable pageable
            ){
        return ResponseEntity.ok(orderService.getOrders(filter, pageable));
    }
}
