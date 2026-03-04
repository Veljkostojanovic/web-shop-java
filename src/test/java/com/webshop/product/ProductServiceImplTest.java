package com.webshop.product;

import com.webshop.category.Category;
import com.webshop.category.CategoryDTO;
import com.webshop.category.CategoryRepository;
import com.webshop.common.exceptions.ResourceConflictException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
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
        void addProduct_shouldThrowIllegalArgumentException() {
            IllegalArgumentException exception = Assertions.assertThrows(
                    IllegalArgumentException.class, () -> productService.addProduct(null));

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Product cannot be null", exception.getMessage());
        }

        @Test
        void addProduct_shouldThrow_ResourceConflictException() {
            when(productRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);

            ResourceConflictException exception = Assertions.assertThrows(
                    ResourceConflictException.class, () -> productService.addProduct(productDTO));

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Product name already exists", exception.getMessage());
            verify(productRepository, never()).save(any());
        }

        @Test
        void addProduct_shouldThrow_ResourceNotFoundException() {
            when(productRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = Assertions.assertThrows(
                    ResourceNotFoundException.class, () -> productService.addProduct(productDTO)
            );

            Assertions.assertNotNull(exception);
            Assertions.assertEquals("Category id not found", exception.getMessage());
            verify(productRepository, never()).save(any());
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
        void existsByNameIgnoreCase_shouldThrowIllegalArgumentException_nameNullorEmpty(){
            IllegalArgumentException exception = Assertions.assertThrows(
                    IllegalArgumentException.class, () -> productService.existsByNameIgnoreCase(null));

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
