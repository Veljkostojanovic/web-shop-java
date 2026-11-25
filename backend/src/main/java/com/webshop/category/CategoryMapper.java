package com.webshop.category;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(Category category) {
        if (category == null) return null;

        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }

    public static CategoryAdminResponse toAdminResponse(Category category) {
        if (category == null) return null;

        return new CategoryAdminResponse(
                category.getId(),
                category.getName(),
                category.getStatus(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

    public static Category toEntity(CategoryCreateRequest categoryCreateRequest) {
        if (categoryCreateRequest == null) return null;
        return new Category(categoryCreateRequest.name());
    }
}
