package com.webshop.admin;

import com.webshop.JWT.UserPrincipal;
import com.webshop.user.UserIdentity;
import com.webshop.user.UserResponse;
import com.webshop.user.UserService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> disableUser(@PathVariable @Positive Long id,
                                            @AuthenticationPrincipal UserPrincipal admin) {
        userService.disableUser(id, admin.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{id}/enable")
    public ResponseEntity<Void> enableUser(@PathVariable @Positive Long id) {
        userService.enableUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/by-username")
    public ResponseEntity<UserResponse> getUserByUsername(
            @RequestParam @NotBlank @Size(max = UserIdentity.MAX_USERNAME_LENGTH) String username) {
        return ResponseEntity.ok(userService.findUserByUsername(username));
    }

    @GetMapping("/users/by-email")
    public ResponseEntity<UserResponse> getUserByEmail(
            @RequestParam @NotBlank @Email @Size(max = UserIdentity.MAX_EMAIL_LENGTH) String email) {
        return ResponseEntity.ok(userService.findUserByEmail(email));
    }
}