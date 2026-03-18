package com.webshop.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webshop.JWT.CustomUserDetailsService;
import com.webshop.JWT.JwtService;
import com.webshop.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.http.MediaType;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({com.fasterxml.jackson.databind.ObjectMapper.class, com.webshop.common.exceptions.GlobalExceptionHandler.class})
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    @Nested
    @DisplayName("Add Category Tests")
    class AddCategoryTests {

        @Test
        void addCategory_shouldReturn201AndCategory() throws Exception {
            CategoryDTO request = new CategoryDTO(null, "Category");
            CategoryDTO response = new CategoryDTO(1L, "Category");

            when(categoryService.addCategory(any(CategoryDTO.class)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Category"));

            verify(categoryService).addCategory(any(CategoryDTO.class));
        }

        @Test
        void addCategory_shouldReturn400() throws Exception {
            CategoryDTO invalidRequest = new CategoryDTO(null, "");

            mockMvc.perform(post("/api/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(categoryService);
        }
    }

    @Nested
    @DisplayName("Update Category Tests")
    class UpdateCategoryTests {

        @Test
        void updateCategory_shouldReturnCategory() throws Exception {
            CategoryDTO request = new CategoryDTO(1L, "New Category");

            when(categoryService.updateCategory(anyLong(), any(CategoryDTO.class)))
                    .thenReturn(request);

            mockMvc.perform(put("/api/categories/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("New Category"));

            verify(categoryService).updateCategory(anyLong(), any(CategoryDTO.class));
        }
    }

    @Nested
    @DisplayName("Reading Category Tests")
    class ReadCategoryTests {


        @Test
        void getCategoryById_shouldReturnCategory() throws Exception {
            CategoryDTO dto = new CategoryDTO(1L, "Category");

            when(categoryService.getCategoryById(anyLong()))
                    .thenReturn(dto);

            mockMvc.perform(get("/api/categories/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Category"));

            verify(categoryService).getCategoryById(anyLong());
        }

        @Test
        void getCategoryById_shouldReturn404() throws Exception {
            when(categoryService.getCategoryById(anyLong())).thenThrow(new ResourceNotFoundException("Category not found"));

            mockMvc.perform(get("/api/categories/{id}", 1L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Category not found"));
        }

        @Test
        void getCategoryById_shouldReturn400() throws Exception {
            mockMvc.perform(get("/api/categories/{id}", "abc"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void getAllCategories_shouldReturnList() throws Exception {
            List<CategoryDTO> categories = List.of(
                    new CategoryDTO(1L, "Shoes"),
                    new CategoryDTO(2L, "Clothes")
            );

            when(categoryService.getAllCategories())
                    .thenReturn(categories);

            mockMvc.perform(get("/api/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(2))
                    .andExpect(jsonPath("$[0].name").value("Shoes"))
                    .andExpect(jsonPath("$[1].name").value("Clothes"));

            verify(categoryService).getAllCategories();
        }
    }

    @Nested
    @DisplayName("Delete Category Tests")
    class DeleteCategoryTests {

        @Test
        void deleteCategoryById_shouldReturn204() throws Exception {
            doNothing().when(categoryService).deleteCategory(anyLong());

            mockMvc.perform(delete("/api/categories/{id}", 1L))
                    .andExpect(status().isNoContent());

            verify(categoryService).deleteCategory(anyLong());
        }

        @Test
        void deleteCategoryById_shouldReturn404() throws Exception {
            doThrow(new ResourceNotFoundException("Category not found"))
                    .when(categoryService).deleteCategory(anyLong());

            mockMvc.perform(delete("/api/categories/{id}", 1L))
                    .andExpect(status().isNotFound());

            verify(categoryService).deleteCategory(anyLong());
        }
    }


    @Nested
    @DisplayName("Existing category Tests")
    class ExistingCategoryTests {


        @Test
        void categoryExists_shouldReturn200_whenExists() throws Exception {
            when(categoryService.existsByNameIgnoreCase(anyString()))
                    .thenReturn(true);

            mockMvc.perform(get("/api/categories/exists")
                            .param("name", "Category"))
                    .andExpect(status().isOk());

            verify(categoryService).existsByNameIgnoreCase(anyString());
        }

        @Test
        void categoryExists_shouldReturn404_whenNotExists() throws Exception {
            when(categoryService.existsByNameIgnoreCase(anyString()))
                    .thenReturn(false);

            mockMvc.perform(get("/api/categories/exists")
                            .param("name", "Category"))
                    .andExpect(status().isNotFound());

            verify(categoryService).existsByNameIgnoreCase(anyString());
        }
    }
}
