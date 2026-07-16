package com.webshop.product;

import com.webshop.category.Category;
import com.webshop.category.CategoryDTO;
import com.webshop.category.CategoryRepository;
import com.webshop.common.exceptions.ResourceConflictException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;


    private Product product;
    private ProductDTO productDTO;
    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    public void setUp() {
        category = new Category();
        categoryDTO = new CategoryDTO();

        category.setId(1L);
        categoryDTO.setId(1L);
        category.setName("Category");
        categoryDTO.setName("Category");

        product = new Product(1L, "Product", "Description", BigDecimal.valueOf(500), 10, category);
        productDTO = new ProductDTO(1L, "Product", "Description", BigDecimal.valueOf(500), 10, categoryDTO);
    }


    @Nested
    @DisplayName("Add Product Tests")
    class AddProductTests{

        @Test
        void addProduct_Ok() {
            when(productRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
            when(productRepository.save(any(Product.class))).thenReturn(product);

            ProductDTO result = productService.addProduct(productDTO);

            Assertions.assertNotNull(result);
            Assertions.assertEquals("Product", result.getName());
            Assertions.assertEquals(BigDecimal.valueOf(500), result.getPrice());
            Assertions.assertEquals("Description", result.getDescription());
            Assertions.assertEquals(10, result.getStock());

            verify(productRepository).save(any(Product.class));
        }

        @Test
        void addProduct_shouldThrow_IllegalArgumentException_productIsNull() {
            IllegalArgumentException exception = Assertions.assertThrows(
                    IllegalArgumentException.class, () -> productService.addProduct(null));

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Product cannot be null", exception.getMessage());
        }

        @Test
        void addProduct_shouldThrow_ResourceConflictException_productAlreadyExists() {
            when(productRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);

            ResourceConflictException exception = Assertions.assertThrows(
                    ResourceConflictException.class, () -> productService.addProduct(productDTO));

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Product name already exists", exception.getMessage());
            verify(productRepository, never()).save(any());
        }

        @Test
        void addProduct_shouldThrow_ResourceNotFoundException_categoryDoesNotExist() {
            when(productRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = Assertions.assertThrows(
                    ResourceNotFoundException.class, () -> productService.addProduct(productDTO)
            );

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Category not found with id: 1", exception.getMessage());
            verify(productRepository, never()).save(any());
        }

        @Test
        void addProduct_NoCategoryId_SavesProductWithoutCategory() {
            productDTO.getCategory().setId(null);

            when(productRepository.existsByNameIgnoreCase(any())).thenReturn(false);
            when(productRepository.save(any(Product.class))).thenReturn(new Product());

            productService.addProduct(productDTO);

            verify(categoryRepository, never()).findById(anyLong());
            verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        void addProduct_CategoryObjectIsNull_SavesProductWithNullCategory() {
            productDTO.setCategory(null);

            when(productRepository.existsByNameIgnoreCase(any())).thenReturn(false);
            when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArguments()[0]);

            productService.addProduct(productDTO);

            verify(categoryRepository, never()).findById(anyLong());
            verify(productRepository).save(argThat(product -> product.getCategory() == null));
        }
    }

    @Nested
    @DisplayName("Update Product Tests")
    class UpdateProductTests{

        @Test
        void updateProduct_Ok() {
            ProductDTO request =  new ProductDTO(1L, "New Product",
                    "New Description", BigDecimal.valueOf(10), 1, categoryDTO);

            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
            when(productRepository.save(any(Product.class))).thenReturn(product);

            ProductDTO result = productService.updateProduct(1L, request);

            Assertions.assertNotNull(result);
            Assertions.assertEquals("New Product", result.getName());
            Assertions.assertEquals(BigDecimal.valueOf(10), result.getPrice());
            Assertions.assertEquals("New Description", result.getDescription());
            Assertions.assertEquals(1, result.getStock());
            verify(productRepository).save(any(Product.class));
        }

        @Test
        void updateProduct_shouldThrowIllegalArgumentException_productIdNull() {
            IllegalArgumentException exception = Assertions.assertThrows(
                    IllegalArgumentException.class, () -> productService.updateProduct(null, productDTO));

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Product id cannot be null", exception.getMessage());

            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        void updateProduct_shouldThrowIllegalArgumentException_productDTONull() {
            IllegalArgumentException exception = Assertions.assertThrows(
                    IllegalArgumentException.class, () -> productService.updateProduct(1L, null));

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Product cannot be null", exception.getMessage());

            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        void updateProduct_shouldThrowResourceNotFoundException_productNotFound(){
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = Assertions.assertThrows(
                    ResourceNotFoundException.class, () -> productService.updateProduct(1L, productDTO)
            );

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Product id not found", exception.getMessage());
            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        void updateProduct_shouldThrowResourceNotFoundException_categoryNotFound(){
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = Assertions.assertThrows(
                    ResourceNotFoundException.class, () -> productService.updateProduct(1L, productDTO)
            );

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Category not found", exception.getMessage());
            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        void updateProduct_categoryIdIsNull(){
            productDTO.setCategory(null);
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));


            Assertions.assertDoesNotThrow(() -> productService.updateProduct(1L, productDTO));
            verify(productRepository).save(any(Product.class));
            verify(categoryRepository, never()).findById(anyLong());
        }

        @Test
        void updateProduct_categoryHasNoId(){
            productDTO.getCategory().setId(null);
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));


            Assertions.assertDoesNotThrow(() -> productService.updateProduct(1L, productDTO));
            verify(productRepository).save(any(Product.class));
            verify(categoryRepository, never()).findById(anyLong());
        }
    }

    @Nested
    @DisplayName("Reading Product Tests")
    class ReadProductTests{

        @Test
        void getProductById_Ok(){
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

            ProductDTO result = productService.getProductById(1L);

            Assertions.assertNotNull(result);
            Assertions.assertEquals("Product", result.getName());
            Assertions.assertEquals(BigDecimal.valueOf(500), result.getPrice());
            Assertions.assertEquals("Description", result.getDescription());
            Assertions.assertEquals(10, result.getStock());
            Assertions.assertEquals(1, result.getId());

            verify(productRepository).findById(1L);
        }

        @Test
        void getProductById_throwsIllegalArgumentException_productIdNull(){
            IllegalArgumentException exception = Assertions.assertThrows(
                    IllegalArgumentException.class, () -> productService.getProductById(null));

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Product id cannot be null", exception.getMessage());

            verify(productRepository, never()).findById(1L);
        }

        @Test
        void getProductById_throwsResourceNotFoundException_productNotFound(){
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = Assertions.assertThrows(
                    ResourceNotFoundException.class, () -> productService.getProductById(1L));

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Product id not found", exception.getMessage());

            verify(productRepository, times(1)).findById(1L);
        }

        @Test
        void getAllProducts_returnListOfProducts(){
            when(productRepository.findAll()).thenReturn(List.of(product));

            List<ProductDTO> result = productService.getAllProducts();

            Assertions.assertNotNull(result);
            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals(1L, result.getFirst().getId());
            Assertions.assertEquals(10, result.getFirst().getStock());
            Assertions.assertEquals("Product", result.getFirst().getName());
            Assertions.assertEquals("Description", result.getFirst().getDescription());
            Assertions.assertEquals(BigDecimal.valueOf(500), result.getFirst().getPrice());

            verify(productRepository).findAll();
        }

        @Test
        void getAllProducts_returnsEmptyList(){
            when(productRepository.findAll()).thenReturn(List.of());

            List<ProductDTO> result = productService.getAllProducts();

            Assertions.assertNotNull(result);
            Assertions.assertEquals(0, result.size());
            verify(productRepository).findAll();
        }

        @Test
        void getAllProducts_pageable_returnPage(){
            org.springframework.data.domain.PageImpl<Product> page = new org.springframework.data.domain.PageImpl<>(List.of(product), org.springframework.data.domain.PageRequest.of(0,10), 1);
            when(productRepository.findAll(org.mockito.ArgumentMatchers.any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

            org.springframework.data.domain.Page<ProductDTO> result = productService.getAllProducts(org.springframework.data.domain.PageRequest.of(0,10));

            Assertions.assertNotNull(result);
            Assertions.assertEquals(1, result.getTotalElements());
            Assertions.assertEquals(1, result.getContent().size());
            Assertions.assertEquals(1L, result.getContent().get(0).getId());
            Assertions.assertEquals("Product", result.getContent().get(0).getName());
            verify(productRepository).findAll(org.mockito.ArgumentMatchers.any(org.springframework.data.domain.Pageable.class));
        }

        @Test
        void getAllProducts_pageable_returnsEmptyPage(){
            org.springframework.data.domain.PageImpl<Product> page = new org.springframework.data.domain.PageImpl<>(List.of(), org.springframework.data.domain.PageRequest.of(0,10), 0);
            when(productRepository.findAll(org.mockito.ArgumentMatchers.any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

            org.springframework.data.domain.Page<ProductDTO> result = productService.getAllProducts(org.springframework.data.domain.PageRequest.of(0,10));

            Assertions.assertNotNull(result);
            Assertions.assertEquals(0, result.getTotalElements());
            Assertions.assertEquals(0, result.getContent().size());
            verify(productRepository).findAll(org.mockito.ArgumentMatchers.any(org.springframework.data.domain.Pageable.class));
        }

        @Test
        void findByCategoryId_returnsListOfProducts(){
            when(productRepository.findByCategoryId(anyLong())).thenReturn(List.of(product));

            List<ProductDTO> result = productService.findByCategoryId(1L);

            Assertions.assertNotNull(result);
            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals(1L, result.getFirst().getId());
            Assertions.assertEquals(10, result.getFirst().getStock());
            Assertions.assertEquals("Product", result.getFirst().getName());
            Assertions.assertEquals("Description", result.getFirst().getDescription());
            Assertions.assertEquals(BigDecimal.valueOf(500), result.getFirst().getPrice());

            verify(productRepository).findByCategoryId(1L);
        }

        @Test
        void findByCategoryId_throwsIllegalArgumentException_categoryIdNull(){
            IllegalArgumentException exception = Assertions.assertThrows(
                    IllegalArgumentException.class, () -> productService.findByCategoryId(null));

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Category id cannot be null", exception.getMessage());
            verify(productRepository, never()).findByCategoryId(anyLong());
        }

        @Test
        void findByNameContainingIgnoreCase_returnsListOfProducts(){
            when(productRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(List.of(product));

            List<ProductDTO> result = productService.findByNameContainingIgnoreCase("Product");

            Assertions.assertNotNull(result);
            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals(1L, result.getFirst().getId());
            Assertions.assertEquals(10, result.getFirst().getStock());
            Assertions.assertEquals("Product", result.getFirst().getName());
            Assertions.assertEquals("Description", result.getFirst().getDescription());
            Assertions.assertEquals(BigDecimal.valueOf(500), result.getFirst().getPrice());

            verify(productRepository).findByNameContainingIgnoreCase(anyString());
        }

        @Test
        void existsByNameIgnoreCase_Ok(){
            when(productRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);
            boolean result = productService.existsByNameIgnoreCase("Product");

            Assertions.assertTrue(result);

            verify(productRepository).existsByNameIgnoreCase(anyString());
        }

        @Test
        void existsByNameIgnoreCase_shouldThrowIllegalArgumentException_nameIsNull(){
            IllegalArgumentException exception = Assertions.assertThrows(
                    IllegalArgumentException.class, () -> productService.existsByNameIgnoreCase(null));

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Name cannot be null or empty", exception.getMessage());
            verify(productRepository, never()).existsByNameIgnoreCase(anyString());
        }

        @Test
        void existsByNameIgnoreCase_shouldThrowIllegalArgumentException_nameIsEmpty(){
            IllegalArgumentException exception = Assertions.assertThrows(
                    IllegalArgumentException.class, () -> productService.existsByNameIgnoreCase(""));

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Name cannot be null or empty", exception.getMessage());
            verify(productRepository, never()).existsByNameIgnoreCase(anyString());
        }
    }

    @Nested
    @DisplayName("Delete product Tests")
    class DeleteProductTests{

        @Test
        void deleteProductById_Ok(){
            when(productRepository.existsById(anyLong())).thenReturn(true);
            productService.deleteProduct(1L);
            verify(productRepository).deleteById(anyLong());
        }

        @Test
        void deleteProductById_shouldThrowIllegalArgumentException_productIdNull(){

            IllegalArgumentException exception = Assertions.assertThrows(
                    IllegalArgumentException.class, () -> productService.deleteProduct(null)
            );
            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Product id cannot be null", exception.getMessage());

            verify(productRepository, never()).deleteById(anyLong());
        }

        @Test
        void deleteProductById_shouldThrowResourceNotFoundException_productIdNotFound(){
            when(productRepository.existsById(anyLong())).thenReturn(false);

            ResourceNotFoundException exception = Assertions.assertThrows(
                    ResourceNotFoundException.class, () -> productService.deleteProduct(1L)
            );

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Product id not found", exception.getMessage());

            verify(productRepository, never()).deleteById(anyLong());
        }
    }
}
