package com.webshop.product;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

public class ProductMapperTest {

    @Test
    void toDTO_Null() {
        ProductDTO result = ProductMapper.toDTO(null);
        Assertions.assertNull(result);
    }

    @Test
    void toEntity_Null(){
        Product result = ProductMapper.toEntity(null);
        Assertions.assertNull(result);
    }

    @Test
    void toDTO_Ok(){
        Product product = new Product(1L, "Product", "Description", BigDecimal.valueOf(100), 10, null);

        ProductDTO result = ProductMapper.toDTO(product);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getName(), product.getName());
        Assertions.assertEquals(result.getDescription(), product.getDescription());
        Assertions.assertEquals(result.getPrice(), product.getPrice());
        Assertions.assertEquals(result.getStock(), product.getStock());
        Assertions.assertEquals(result.getId(), product.getId());
    }

    @Test
    void toEntity_Ok(){
        ProductDTO  productDTO = new ProductDTO(1L, "Product", "Description", BigDecimal.valueOf(100), 10, null);
        Product result = ProductMapper.toEntity(productDTO);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getName(), productDTO.getName());
        Assertions.assertEquals(result.getDescription(), productDTO.getDescription());
        Assertions.assertEquals(result.getPrice(), productDTO.getPrice());
        Assertions.assertEquals(result.getStock(), productDTO.getStock());
        Assertions.assertEquals(result.getId(), productDTO.getId());
    }

}
