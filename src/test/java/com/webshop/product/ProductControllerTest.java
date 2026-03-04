package com.webshop.product;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.webshop.JWT.CustomUserDetailsService;
import com.webshop.JWT.JwtService;
import com.webshop.category.CategoryDTO;
import com.webshop.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({com.fasterxml.jackson.databind.ObjectMapper.class, com.webshop.common.exceptions.GlobalExceptionHandler.class})
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private ProductDTO productDTO;
    private CategoryDTO categoryDTO;
    @BeforeEach
    void setUp() {
        categoryDTO = new CategoryDTO(1L, "Category");
        productDTO = new ProductDTO(1L, "Product", "Description", BigDecimal.valueOf(500), 10, categoryDTO);
    }

    @Nested
    @DisplayName("Add Product Test")
    class AddProductTests{

        @Test
        void addProduct_ShouldReturn201AndProduct() throws Exception{
            when(productService.addProduct(any(ProductDTO.class))).thenReturn(productDTO);

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(productDTO.getName()))
                    .andExpect(jsonPath("$.description").value(productDTO.getDescription()))
                    .andExpect(jsonPath("$.price").value(productDTO.getPrice()))
                    .andExpect(jsonPath("$.id").value(productDTO.getId()))
                    .andExpect(jsonPath("$.stock").value(productDTO.getStock()));

            verify(productService).addProduct(any(ProductDTO.class));
        }

        @Test
        void addProduct_ShouldReturn400_BadRequest() throws Exception{
            ProductDTO badDTO = new ProductDTO(1L, "", "", BigDecimal.valueOf(0), 0, null);

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(badDTO)))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(productService);
        }
    }

    @Nested
    @DisplayName("Reading Product Tests")
    class ReadProductTests{

        @Test
        void getAllProducts_EmptyList() throws Exception{
            when(productService.getAllProducts()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(productService).getAllProducts();
        }

        @Test
        void getAllProducts_shouldReturnsList() throws Exception{
            List<ProductDTO> productDTOList = List.of(productDTO);

            when(productService.getAllProducts()).thenReturn(productDTOList);

            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].name").value(productDTOList.getFirst().getName()))
                    .andExpect(jsonPath("$[0].description").value(productDTOList.getFirst().getDescription()))
                    .andExpect(jsonPath("$[0].price").value(productDTOList.getFirst().getPrice()))
                    .andExpect(jsonPath("$[0].id").value(productDTOList.getFirst().getId()));

            verify(productService).getAllProducts();
        }

        @Test
        void getProductById_shoudlReturn400() throws Exception{
            mockMvc.perform(get("/api/products/{id}", "abc"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(productService);
        }

        @Test
        void getProductById_shouldReturn404() throws Exception{
            when(productService.getProductById(anyLong())).thenThrow(new ResourceNotFoundException("Product not found"));

            mockMvc.perform(get("/api/products/{id}", 1L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Product not found"));
        }

        @Test
        void getProductById_shouldReturnProduct() throws Exception{
            when(productService.getProductById(anyLong())).thenReturn(productDTO);

            mockMvc.perform(get("/api/products/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(productDTO.getName()))
                    .andExpect(jsonPath("$.description").value(productDTO.getDescription()))
                    .andExpect(jsonPath("$.price").value(productDTO.getPrice()))
                    .andExpect(jsonPath("$.id").value(productDTO.getId()));
            verify(productService).getProductById(anyLong());
        }

        @Test
        void findByCategoryId_shouldReturnList() throws Exception{
            List<ProductDTO> productDTOList = List.of(productDTO);

            when(productService.findByCategoryId(anyLong())).thenReturn(productDTOList);

            mockMvc.perform(get("/api/products//by-category/{categoryId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(productDTOList.size()))
                    .andExpect(jsonPath("$[0].name").value(productDTOList.getFirst().getName()));

            verify(productService).findByCategoryId(anyLong());
        }

        @Test
        void findByCategoryId_shouldReturnEmptyList() throws Exception{
            when(productService.findByCategoryId(anyLong())).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/products//by-category/{categoryId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
            verify(productService).findByCategoryId(anyLong());
        }

        @Test
        void searchProductByName_shouldReturnList() throws Exception{
            List<ProductDTO> productDTOList = List.of(productDTO);

            when(productService.findByNameContainingIgnoreCase(anyString())).thenReturn(productDTOList);

            mockMvc.perform(get("/api/products/search")
                    .param("name", anyString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(productDTOList.size()))
                    .andExpect(jsonPath("$[0].name").value(productDTOList.getFirst().getName()));

            verify(productService).findByNameContainingIgnoreCase(anyString());
        }

        @Test
        void searchProductByName_shouldReturnEmptyList() throws Exception{
            when(productService.findByNameContainingIgnoreCase(anyString())).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/products/search")
                            .param("name", anyString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(productService).findByNameContainingIgnoreCase(anyString());
        }

        @Test
        void productExistsByNameIgnoreCase_Ok() throws Exception{
            when(productService.existsByNameIgnoreCase(anyString())).thenReturn(true);

            mockMvc.perform(get("/api/products/exists")
                            .param("name", anyString()))
                    .andExpect(status().isOk());

            verify(productService).existsByNameIgnoreCase(anyString());
        }

        @Test
        void productExistsByNameIgnoreCase_NotFound() throws Exception{
            when(productService.existsByNameIgnoreCase(anyString())).thenReturn(false);

            mockMvc.perform(get("/api/products/exists")
                    .param("name", anyString()))
                    .andExpect(status().isNotFound());

            verify(productService).existsByNameIgnoreCase(anyString());
        }
    }

    @Nested
    @DisplayName("Delete Product Test")
    class DeleteProductTest{

        @Test
        void deleteProductById_shouldReturn204() throws Exception{
            when(productService.deleteProduct(anyLong())).thenReturn(true);

            mockMvc.perform(delete("/api/products/{id}", 1L))
                    .andExpect(status().isNoContent());

            verify(productService).deleteProduct(anyLong());
        }

        @Test
        void deleteProductById_shouldReturn400_badRequest() throws Exception{
            mockMvc.perform(delete("/api/products/{id}", "abc"))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).deleteProduct(anyLong());
        }
    }

    @Nested
    @DisplayName("Update product Test")
    class UpdateProductTest{

        @Test
        void updateProductById_shouldReturn200() throws Exception{
            ProductDTO request = new ProductDTO(1L, "New Product",
                    "New Description", BigDecimal.valueOf(500), 10, categoryDTO);

            when(productService.updateProduct(anyLong(), any(ProductDTO.class))).thenReturn(request);

            mockMvc.perform(put("/api/products/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(request.getName()))
                    .andExpect(jsonPath("$.description").value(request.getDescription()))
                    .andExpect(jsonPath("$.price").value(request.getPrice()))
                    .andExpect(jsonPath("$.category.id").value(request.getCategory().getId()))
                    .andExpect(jsonPath("$.category.name").value(request.getCategory().getName()));

            verify(productService).updateProduct(anyLong(), any(ProductDTO.class));
        }

        @Test
        void updateProductById_shouldReturn400_badId() throws Exception{
            mockMvc.perform(put("/api/products/{id}", "abc"))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).updateProduct(anyLong(), any(ProductDTO.class));
        }

        @Test
        void updateProductbyId_shouldreturn400_badDTO() throws Exception{
            ProductDTO request = new ProductDTO(1L, "", "", BigDecimal.valueOf(500), 0, null);

            when(productService.updateProduct(anyLong(), any(ProductDTO.class))).thenReturn(request);

            mockMvc.perform(put("/api/products/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).updateProduct(anyLong(), any(ProductDTO.class));
        }
    }
}