package com.webshop.admin;


import com.webshop.order.OrderDTO;
import com.webshop.order.OrderFilter;
import com.webshop.order.OrderService;
import com.webshop.user.UserDTO;
import com.webshop.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername (@PathVariable String username){
        return ResponseEntity.ok(userService.findUserByUsername(username));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email){
        return ResponseEntity.ok(userService.findUserByEmail(email));
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderDTO>> getOrders(
            @ParameterObject @Valid OrderFilter filter,
            @ParameterObject Pageable pageable
            ){
        return ResponseEntity.ok(orderService.getOrders(filter, pageable));
    }
}
