package com.webshop.category;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


class CategoryControllerTest {

    RestTestClient client;
    CategoryService categoryService;

    @BeforeEach
    void setUp(){
        categoryService = Mockito.mock(CategoryService.class);

        Mockito.when(categoryService.getAllCategories())
                .thenReturn(
                        List.of(new CategoryDTO(1L, "Patike"))
                );

        client = RestTestClient.bindToController(new CategoryController(categoryService)).build();
    }

    @Test
    void addingCategorySuccessfully() throws Exception {
    }

    @Test
    void getCategoryById() {
    }

    @Test
    void getAllCategories() {
        List<CategoryDTO> allCategories =
                client.get()
                        .uri("/api/categories")
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(new ParameterizedTypeReference<List<CategoryDTO>>() {})
                        .returnResult()
                        .getResponseBody();

        assertThat(allCategories).isNotNull();
        assertThat(allCategories).hasSize(1);
        assertThat(allCategories.get(0).getId()).isEqualTo(1L);
        Mockito.verify(categoryService).getAllCategories();
    }

    @Test
    void deleteCategoryById() {
    }

    @Test
    void categoryExistsByNameIgnoreCase() {
    }
}