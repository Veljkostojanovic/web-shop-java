package com.webshop.product;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilterDto {
    private String name;
    private Long categoryId;

    @Min(value = 0, message = "Min price cannot be negative")
    private BigDecimal minPrice;

    @Min(value = 0, message = "Max price cannot be negative")
    private BigDecimal maxPrice;
    private Integer stock;
}
