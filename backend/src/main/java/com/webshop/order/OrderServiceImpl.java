package com.webshop.order;

import com.webshop.cart.Cart;
import com.webshop.cart.CartRepository;
import com.webshop.cartItem.CartItem;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.product.Product;
import com.webshop.product.ProductRepository;
import com.webshop.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderDTO createOrder(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No cart found for user: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot checkout with an empty cart!");
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

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
                throw new RuntimeException("Not enough stock for: " + product.getName());
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

        return mapToDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow( () -> new ResourceNotFoundException("Order not found with id: " + orderId) );
        return mapToDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public OrderEntity getById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow( () -> new ResourceNotFoundException("Order not found with id: " + orderId) );
    }

    @Override
    public void markAsPaid(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow();

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }

    private OrderDTO mapToDTO(OrderEntity order){
        var itemsDTO = order.getOrderItems().stream()
                .map(this::mapItemToDTO)
                .toList();
        return new OrderDTO(order.getId(), order.getOrderDate(), order.getTotalAmount(), itemsDTO);
    }

    private OrderItemDTO mapItemToDTO(OrderItem item) {
        var itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return new OrderItemDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getPrice(),
                item.getQuantity(),
                itemTotal
        );
    }
}