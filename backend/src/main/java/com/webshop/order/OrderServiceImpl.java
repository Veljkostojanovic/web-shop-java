package com.webshop.order;

import com.webshop.cart.Cart;
import com.webshop.cart.CartRepository;
import com.webshop.cart.CartItem;
import com.webshop.common.exceptions.BadRequestException;
import com.webshop.common.exceptions.InsufficientStockException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.product.Product;
import com.webshop.product.ProductRepository;
import com.webshop.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "totalAmount", "orderDate", "status");

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId) {

        cartRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        Cart cart = cartRepository.findWithItemsByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No cart found for user: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot checkout with an empty cart!");
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id = " + userId));

        List<Long> productIds = cart.getItems()
                .stream()
                .map(item -> item.getProduct().getId())
                .toList();

        Map<Long, Product> productMap = productRepository.findAllByIdInWithLock(productIds)
                .stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        BigDecimal totalAmount = BigDecimal.ZERO;
        OrderEntity order = new OrderEntity();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(new ArrayList<>());

        for (var cartItem : cart.getItems()) {
            var product = productMap.get(cartItem.getProduct().getId());

            if (product.getStock() < cartItem.getQuantity()) {
                throw new InsufficientStockException("Not enough stock for: " + product.getName());
            }
            product.decreaseStock(cartItem.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            order.addOrderItem(orderItem);

            BigDecimal itemSubtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemSubtotal);
        }

        order.setTotalAmount(totalAmount);

        OrderEntity savedOrder = orderRepository.save(order);
        cart.clear();
        cartRepository.save(cart);

        return OrderMapper.mapToDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow( () -> new ResourceNotFoundException("Order not found with id: " + orderId) );
        return OrderMapper.mapToDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUserId(Long userId, OrderFilterRequest filterRequest, Pageable pageable){
        OrderFilter filter = createOrderFilter(userId, filterRequest);
        validateSort(pageable);
        Specification<OrderEntity> spec = OrderSpecification.buildSpecification(filter);
        return orderRepository.findAll(spec, pageable)
                .map(OrderMapper::mapToDTO);
    }

    @Override
    public OrderResponse getOrder(Long orderId, Long userId) {
        OrderEntity order = orderRepository
                .findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id = " + orderId));

        return OrderMapper.mapToDTO(order);
    }

    @Override
    public Page<OrderResponse> getOrders(OrderFilter filter, Pageable pageable) {
        validateSort(pageable);

        Specification<OrderEntity> spec = OrderSpecification.buildSpecification(filter);

        return orderRepository.findAll(spec, pageable)
                .map(OrderMapper::mapToDTO);
    }

    private void validateSort(Pageable pageable) {
        for(Sort.Order order : pageable.getSort()){
            if(!ALLOWED_SORT_FIELDS.contains(order.getProperty())){
                throw new BadRequestException("Invalid sort field: " + order.getProperty());
            }
        }
    }

    private OrderFilter createOrderFilter(Long userId, OrderFilterRequest filterRequest){
        return new OrderFilter(userId,
                filterRequest.startDate(),
                filterRequest.endDate(),
                filterRequest.minPrice(),
                filterRequest.maxPrice(),
                filterRequest.status());
    }
}