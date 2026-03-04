package com.webshop.category;

import com.webshop.category.Category;
import com.webshop.category.CategoryDTO;
import com.webshop.category.CategoryMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CategoryMapperTest {
    @Test
    void toDto(){
        Category category = new Category(1L, "Category", null);

        CategoryDTO categoryDTO = CategoryMapper.toDTO(category);

        Assertions.assertNotNull(categoryDTO);
        Assertions.assertEquals(1L, categoryDTO.getId());
        Assertions.assertEquals("Category", categoryDTO.getName());
    }

    @Test
    void fromDto(){
        CategoryDTO categoryDTO = new CategoryDTO(1L, "Category");

        Category category = CategoryMapper.toEntity(categoryDTO);

        Assertions.assertNotNull(category);
        Assertions.assertNull(category.getId());
        Assertions.assertEquals("Category", category.getName());
        Assertions.assertNull(category.getProducts());
    }
}
