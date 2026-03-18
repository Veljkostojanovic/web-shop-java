package com.webshop.order;

import com.webshop.product.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class OrderItemMapperTest {

    @Test
    void toEntity_Null(){
        OrderItemDTO result = OrderItemMapper.toDTO(null);
        Assertions.assertNull(result);
    }

    @Test
    void  toEntity(){
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setPrice(BigDecimal.valueOf(500));
        orderItem.setQuantity(10);

        OrderItemDTO result = OrderItemMapper.toDTO(orderItem);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(10, result.getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(500), result.getProductPrice());
        Assertions.assertEquals("Product", result.getProductName());
        Assertions.assertEquals(1L, result.getProductId());
        Assertions.assertEquals(BigDecimal.valueOf(5000), result.getTotalPrice());
    }
}
