package com.webshop.category;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CategoryMapperTest {
    @Test
    void toDto() {
        Category category = new Category(1L, "Category", null);

        CategoryResponse categoryResponse = CategoryMapper.toResponse(category);

        Assertions.assertNotNull(categoryResponse);
        Assertions.assertEquals(1L, categoryResponse.getId());
        Assertions.assertEquals("Category", categoryResponse.getName());
    }

    @Test
    void fromDto() {
        CategoryResponse categoryResponse = new CategoryResponse(1L, "Category");

        Category category = CategoryMapper.toEntity(categoryResponse);

        Assertions.assertNotNull(category);
        Assertions.assertNull(category.getId());
        Assertions.assertEquals("Category", category.getName());
        Assertions.assertNull(category.getProducts());
    }

    @Test
    void toDTO_null() {
        CategoryResponse categoryResponse = CategoryMapper.toResponse(null);
        Assertions.assertNull(categoryResponse);
    }

    @Test
    void toEntity_null() {
        Category category = CategoryMapper.toEntity(null);
        Assertions.assertNull(category);
    }
}
