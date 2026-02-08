package com.webshop.order;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.webshop.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OrderEntity {
    @Id @GeneratedValue private Long id;

    @ManyToOne
    private User user;

    private LocalDateTime orderDate;
    private double totalAmount;

    @OneToMany(mappedBy = "orderEntity")
    private List<OrderItem> orderItems;
}
