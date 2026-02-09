package com.webshop.product;

import com.webshop.category.CategoryDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long Id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private CategoryDTO category;
}
