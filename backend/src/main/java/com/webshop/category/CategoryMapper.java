package com.webshop.category;

public class CategoryMapper {

    public static CategoryResponse toResponse(Category category) {
        if(category == null) return null;

        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }

    public static Category toEntity(CategoryCreateRequest categoryCreateRequest) {
        if(categoryCreateRequest == null) return null;
        return new Category(categoryCreateRequest.name());
    }
}
