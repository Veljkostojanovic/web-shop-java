package com.webshop.order;

import com.webshop.cart.Cart;
import com.webshop.cart.CartRepository;
import com.webshop.cartItem.CartItem;
import com.webshop.common.exceptions.BadRequestException;
import com.webshop.common.exceptions.InsufficientResourcesException;
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
    public OrderDTO createOrder(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No cart found for user: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot checkout with an empty cart!");
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Long> productIds = cart.getItems().stream().map(CartItem::getProductId).toList();

        Map<Long, Product> productMap = productRepository.findAllByIdInWithLock(productIds)
                .stream().collect(Collectors.toMap(Product::getId, p -> p));

        BigDecimal totalAmount = BigDecimal.ZERO;

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(new ArrayList<>());

        for (var cartItem : cart.getItems()) {
            var product = productMap.get(cartItem.getProductId());

            if (product.getStock() < cartItem.getQuantity()) {
                throw new InsufficientResourcesException("Not enough stock for: " + product.getName());
            }
            product.setStock(product.getStock() - cartItem.getQuantity());

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

        cart.getItems().clear();
        cartRepository.save(cart);

        return OrderMapper.mapToDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow( () -> new ResourceNotFoundException("Order not found with id: " + orderId) );
        return OrderMapper.mapToDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByUserId(Long userId, OrderFilterRequest filterRequest, Pageable pageable){
        OrderFilter filter = createOrderFilter(userId, filterRequest);
        validateSort(pageable);
        Specification<OrderEntity> spec = OrderSpecification.buildSpecification(filter);
        return orderRepository.findAll(spec, pageable)
                .map(OrderMapper::mapToDTO);
    }

    @Override
    public OrderDTO getOrder(Long orderId, Long userId) {
        OrderEntity order = orderRepository
                .findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return OrderMapper.mapToDTO(order);
    }

    @Override
    public Page<OrderDTO> getOrders(OrderFilter filter, Pageable pageable) {
        validateSort(pageable);

        Specification<OrderEntity> spec = OrderSpecification.buildSpecification(filter);

        return orderRepository.findAll(spec, pageable)
                .map(OrderMapper::mapToDTO);
    }

    private Pageable validateSort(Pageable pageable) {
        for(Sort.Order order : pageable.getSort()){
            if(!ALLOWED_SORT_FIELDS.contains(order.getProperty())){
                throw new BadRequestException("Invalid sort field: " + order.getProperty());
            }
        }
        return pageable;
    }

    private OrderFilter createOrderFilter(Long userId, OrderFilterRequest filterRequest){
        OrderFilter orderFilter = new OrderFilter();
        orderFilter.setUserId(userId);
        orderFilter.setStatus(filterRequest.getStatus());
        orderFilter.setMinPrice(filterRequest.getMinPrice());
        orderFilter.setMaxPrice(filterRequest.getMaxPrice());
        orderFilter.setStartDate(filterRequest.getStartDate());
        orderFilter.setEndDate(filterRequest.getEndDate());
        return orderFilter;
    }
}