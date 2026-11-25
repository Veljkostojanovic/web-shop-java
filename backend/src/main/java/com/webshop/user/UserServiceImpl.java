package com.webshop.user;

import com.webshop.common.exceptions.BadRequestException;
import com.webshop.common.exceptions.ResourceConflictException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.common.persistence.ConstraintViolations;
import com.webshop.common.util.SafePageables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "username", "email");
    private static final String EMAIL_CONSTRAINT = "uk_users_email";
    private static final String USERNAME_CONSTRAINT = "uk_users_username";

    private final UserRepository userRepository;

    private static RuntimeException translateIdentityConflict(DataIntegrityViolationException e) {
        if (ConstraintViolations.isViolationOf(e, EMAIL_CONSTRAINT)) {
            return new ResourceConflictException("Email is already registered");
        }
        if (ConstraintViolations.isViolationOf(e, USERNAME_CONSTRAINT)) {
            return new ResourceConflictException("Username is already taken");
        }
        return e;
    }

    @Override
    @Transactional
    public UserResponse createUser(String email, String username, String passwordHash) {
        String normalizedEmail = UserIdentity.normalizeEmail(email);
        String normalizedUsername = UserIdentity.normalizeUsername(username);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResourceConflictException("Email is already registered");
        }
        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new ResourceConflictException("Username is already taken");
        }

        User saved;
        try {
            saved = userRepository.saveAndFlush(
                    new User(normalizedEmail, normalizedUsername, passwordHash, UserRole.USER));
        } catch (DataIntegrityViolationException e) {
            throw translateIdentityConflict(e);
        }

        log.info("User registered: id={}", saved.getId());
        return UserMapper.toResponse(saved);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public UserResponse getCurrentUser(Long userId) {
        return UserMapper.toResponse(findUserOrThrow(userId));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse findUserByEmail(String email) {
        return userRepository.findByEmail(UserIdentity.normalizeEmail(email))
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse findUserByUsername(String username) {
        return userRepository.findByUsername(UserIdentity.normalizeUsername(username))
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Pageable safe = SafePageables.withSafeSort(pageable, ALLOWED_SORT_FIELDS, Sort.by("id"));
        return userRepository.findAll(safe).map(UserMapper::toResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void disableUser(Long userId, Long actingAdminId) {
        if (userId.equals(actingAdminId)) {
            throw new BadRequestException("Admins cannot disable their own account");
        }
        User user = findUserOrThrow(userId);
        user.disable();
        log.info("User disabled: id={}, byAdminId={}", userId, actingAdminId);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void enableUser(Long userId) {
        User user = findUserOrThrow(userId);
        user.enable();
        log.info("User enabled: id={}", userId);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }
}