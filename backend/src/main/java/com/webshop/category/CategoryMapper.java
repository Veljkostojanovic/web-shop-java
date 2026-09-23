package com.webshop.category;

public class CategoryMapper {

    public static CategoryResponse toDTO(Category category) {
        if(category == null) return null;

        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }

    public static Category toEntity(CategoryResponse categoryResponse) {
        if(categoryResponse == null) return null;
        Category category = new Category();
        category.setName(categoryResponse.name());
        return category;
    }

    public static Category toEntity(CategoryCreateRequest categoryCreateRequest) {
        if(categoryCreateRequest == null) return null;
        Category category = new Category();
        category.setName(categoryCreateRequest.name());
        return category;
    }
}
