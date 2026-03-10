package com.webshop.category;

public class CategoryMapper {

    public static CategoryDTO toDTO(Category category) {
        if(category == null) return null;
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        return categoryDTO;
    }

    public static Category toEntity(CategoryDTO categoryDTO) {
        if(categoryDTO == null) return null;
        Category category = new Category();
        category.setName(categoryDTO.getName());
        return category;
    }
}
