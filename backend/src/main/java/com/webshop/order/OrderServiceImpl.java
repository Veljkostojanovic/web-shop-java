package com.webshop.order;

import com.webshop.cart.Cart;
import com.webshop.cart.CartItem;
import com.webshop.cart.CartRepository;
import com.webshop.common.exceptions.BadRequestException;
import com.webshop.common.exceptions.ResourceConflictException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.common.util.SafePageables;
import com.webshop.product.Product;
import com.webshop.product.ProductRepository;
import com.webshop.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "createdAt", "totalAmount", "status");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private static ResourceConflictException emptyCart() {
        return new ResourceConflictException("Cannot check out an empty cart");
    }

    private static ResourceNotFoundException orderNotFound(Long orderId) {
        return new ResourceNotFoundException("Order not found with ID: " + orderId);
    }

    private static void requireValidRanges(Instant createdFrom, Instant createdTo,
                                           BigDecimal minTotal, BigDecimal maxTotal) {
        if (createdFrom != null && createdTo != null && !createdFrom.isBefore(createdTo)) {
            throw new BadRequestException("createdFrom must be before createdTo");
        }
        if (minTotal != null && maxTotal != null && minTotal.compareTo(maxTotal) > 0) {
            throw new BadRequestException("minTotal must not be greater than maxTotal");
        }
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public OrderResponse createOrder(Long userId) {

        cartRepository.findByUserIdForUpdate(userId)
                .orElseThrow(OrderServiceImpl::emptyCart);

        List<Long> productIds = cartRepository.findProductIdsByUserId(userId);
        if (productIds.isEmpty()) {
            throw emptyCart();
        }

        Map<Long, Product> lockedProducts = productRepository.findAllByIdInWithLock(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        Cart cart = cartRepository.findWithItemsByUserId(userId)
                .orElseThrow(OrderServiceImpl::emptyCart);

        OrderEntity order = new OrderEntity(userRepository.getReferenceById(userId));
        for (CartItem item : cart.getItems()) {
            Product product = lockedProducts.get(item.getProduct().getId());
            if (product == null) {
                throw new IllegalStateException("Product " + item.getProduct().getId() + " was not locked");
            }
            product.decreaseStock(item.getQuantity());
            order.addLine(product, item.getQuantity());
        }

        OrderEntity saved = orderRepository.save(order);
        cart.clear();

        log.info("Order placed: id={}, userId={}, lines={}, total={}",
                saved.getId(), userId, saved.getOrderItems().size(), saved.getTotalAmount());
        return OrderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public OrderResponse getOrder(Long orderId, Long userId) {
        return orderRepository.findWithItemsByIdAndUserId(orderId, userId)
                .map(OrderMapper::toResponse)
                .orElseThrow(() -> orderNotFound(orderId));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public Page<OrderResponse> getOrdersForUser(Long userId, OrderFilter filter, Pageable pageable) {
        if (filter != null) {
            requireValidRanges(filter.createdFrom(), filter.createdTo(), filter.minTotal(), filter.maxTotal());
        }
        Pageable safe = SafePageables.withSafeSort(pageable, ALLOWED_SORT_FIELDS, DEFAULT_SORT);
        return orderRepository.findAll(OrderSpecification.forCustomer(userId, filter), safe)
                .map(OrderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<OrderAdminResponse> adminSearch(AdminOrderFilter filter, Pageable pageable) {
        if (filter != null) {
            requireValidRanges(filter.createdFrom(), filter.createdTo(), filter.minTotal(), filter.maxTotal());
        }
        Pageable safe = SafePageables.withSafeSort(pageable, ALLOWED_SORT_FIELDS, DEFAULT_SORT);
        return orderRepository.findAll(OrderSpecification.forAdmin(filter), safe)
                .map(OrderMapper::toAdminResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public OrderAdminResponse getOrderForAdmin(Long orderId) {
        return orderRepository.findWithItemsById(orderId)
                .map(OrderMapper::toAdminResponse)
                .orElseThrow(() -> orderNotFound(orderId));
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        OrderEntity order = orderRepository.findByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> orderNotFound(orderId));

        order.cancelByCustomer();
        restock(order);

        log.info("Order cancelled by customer: id={}, userId={}", orderId, userId);
        return OrderMapper.toResponse(order);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public OrderAdminResponse markOrderPaid(Long orderId) {
        return changeStatus(orderId, OrderEntity::markPaid);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public OrderAdminResponse shipOrder(Long orderId) {
        return changeStatus(orderId, OrderEntity::ship);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public OrderAdminResponse deliverOrder(Long orderId) {
        return changeStatus(orderId, OrderEntity::deliver);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public OrderAdminResponse cancelOrderAsAdmin(Long orderId) {
        OrderEntity order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> orderNotFound(orderId));

        order.cancel();
        restock(order);

        log.info("Order cancelled by admin: id={}", orderId);
        return OrderMapper.toAdminResponse(order);
    }

    private OrderAdminResponse changeStatus(Long orderId, Consumer<OrderEntity> transition) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> orderNotFound(orderId));

        OrderStatus before = order.getStatus();
        transition.accept(order);

        log.info("Order status changed: id={}, {} -> {}", orderId, before, order.getStatus());
        return OrderMapper.toAdminResponse(order);
    }

    private void restock(OrderEntity order) {
        List<Long> productIds = orderRepository.findProductIdsByOrderId(order.getId());
        Map<Long, Product> lockedProducts = productRepository.findAllByIdInWithLock(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (OrderItem line : order.getOrderItems()) {
            lockedProducts.get(line.getProduct().getId()).increaseStock(line.getQuantity());
        }
    }
}