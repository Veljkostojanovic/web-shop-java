package com.webshop.category;

import com.webshop.common.exceptions.ResourceConflictException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    public void setup() {
        category = new Category();
        category.setId(1L);
        category.setName("Category");

        categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Category");
    }

    @Nested
    @DisplayName("Add Category Tests")
    class AddCategoryTest {

        @Test
        void addCategory_Ok() {
            when(categoryRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenReturn(category);


            CategoryDTO savedDTO = categoryService.addCategory(categoryDTO);

            Assertions.assertNotNull(savedDTO);
            Assertions.assertEquals("Category", savedDTO.getName());
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        void addCategory_ThrowsException_AlreadyExists() throws Exception {
            when(categoryRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);

            Assertions.assertThrows(ResourceConflictException.class, () -> categoryService.addCategory(categoryDTO));
            verify(categoryRepository, never()).save(any());
        }

        @Test
        void addCategory_ThrowsException_Null() throws Exception {
            Assertions.assertThrows(IllegalArgumentException.class, () -> categoryService.addCategory(null));
            verify(categoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update category Tests")
    class UpdateCategoryTest {

        @Test
        void updateCategory_Ok() throws Exception {
            CategoryDTO updateDto = new CategoryDTO();
            updateDto.setName("New Category");

            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

            CategoryDTO result = categoryService.updateCategory(1L, updateDto);

            Assertions.assertEquals("New Category", result.getName());
            Assertions.assertEquals(1L, result.getId());
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        void updateCategory_ThrowIllegalArgumentException_NullId() throws Exception {
            IllegalArgumentException exception = Assertions.assertThrows
                    (IllegalArgumentException.class, () -> categoryService.updateCategory(null, new CategoryDTO()));

            Assertions.assertEquals("CategoryId cannot be null", exception.getMessage());
        }

        @Test
        void updateCategory_ThrowIllegalArgumentException_NullUpdateDTO() throws Exception {
            IllegalArgumentException exception = Assertions.assertThrows(
                    IllegalArgumentException.class, () -> categoryService.updateCategory(1L, null));

            Assertions.assertEquals("CategoryDTO cannot be null", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Reading categories tests")
    class ReadCategoriesTests {

        @Test
        void getCategoryById_Ok() throws Exception {
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

            CategoryDTO result = categoryService.getCategoryById(1L);

            Assertions.assertNotNull(result);
            Assertions.assertEquals("Category", result.getName());
            Assertions.assertEquals(1L, result.getId());

            verify(categoryRepository).findById(anyLong());
        }

        @Test
        void getCategoryById_ThrowIllegalArgumentException_NullId() throws Exception {
            IllegalArgumentException exception = Assertions.assertThrows(
                    IllegalArgumentException.class, () -> categoryService.getCategoryById(null));

            Assertions.assertEquals("CategoryId cannot be null", exception.getMessage());

            verify(categoryRepository, never()).findById(anyLong());
        }

        @Test
        void getCategoryById_ThrowResourceNotFound_NotFound() throws Exception {
            ResourceNotFoundException exception = Assertions.assertThrows(
                    ResourceNotFoundException.class, () -> categoryService.getCategoryById(1L)
            );

            Assertions.assertEquals("Category not found", exception.getMessage());
        }

        @Test
        void getAllCategories_EmptyList() throws Exception {
            when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

            List<CategoryDTO> result = categoryService.getAllCategories();

            Assertions.assertNotNull(result);
            Assertions.assertTrue(result.isEmpty());
            Assertions.assertEquals(0, result.size());

            verify(categoryRepository).findAll();
        }

        @Test
        void getAllCategories_Ok() throws Exception {
            when(categoryRepository.findAll()).thenReturn(List.of(category));

            List<CategoryDTO> result = categoryService.getAllCategories();

            Assertions.assertNotNull(result);
            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals(1L, result.get(0).getId());
            Assertions.assertEquals("Category", result.get(0).getName());

            verify(categoryRepository).findAll();
        }

        @Test
        void existsByNameIgnoreCase_Ok() throws Exception {
            when(categoryRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);
            boolean result = categoryService.existsByNameIgnoreCase("Category");
            Assertions.assertTrue(result);
            verify(categoryRepository).existsByNameIgnoreCase(anyString());
        }

        @Test
        void existsByNameIgnoreCase_False() throws Exception {
            when(categoryRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
            boolean result = categoryService.existsByNameIgnoreCase("Category");
            Assertions.assertFalse(result);
            verify(categoryRepository).existsByNameIgnoreCase(anyString());
        }

        @Test
        void existsByNameIgnoreCase_ThrowIllegalArgument_NullName() throws Exception {
            IllegalArgumentException exception = Assertions.assertThrows(
                    IllegalArgumentException.class, () -> categoryService.existsByNameIgnoreCase(null));

            Assertions.assertEquals("Name cannot be null or empty", exception.getMessage());

            verify(categoryRepository, never()).existsByNameIgnoreCase(anyString());
        }

        @Test
        void existsByNameIgnoteCase_ThrowIllegalArgument_BlankName() throws Exception {
            IllegalArgumentException exception = Assertions.assertThrows(
                    IllegalArgumentException.class, () -> categoryService.existsByNameIgnoreCase("    ")
            );
            Assertions.assertEquals("Name cannot be null or empty", exception.getMessage());

            verify(categoryRepository, never()).existsByNameIgnoreCase(anyString());
        }
    }

    @Nested
    @DisplayName("Delete category tests")
    class DeleteCategoryTests {

        @Test
        void deleteCategory_Ok() throws Exception {
            when(categoryRepository.existsById(anyLong())).thenReturn(true);
            Assertions.assertDoesNotThrow(() -> categoryService.deleteCategory(1L));

            verify(categoryRepository, times(1)).deleteById(anyLong());
        }

        @Test
        void deleteCategory_ThrowResourceNotFound_NotFound() throws Exception {
            when(categoryRepository.existsById(anyLong())).thenReturn(false);

            ResourceNotFoundException exception = Assertions.assertThrows(
                    ResourceNotFoundException.class, () -> categoryService.deleteCategory(1L));

            Assertions.assertEquals("Category not found", exception.getMessage());

            verify(categoryRepository, never()).deleteById(anyLong());
        }

        @Test
        void deleteCategory_ThrowIllegalArgumentException_NullId() throws Exception {
            IllegalArgumentException exception = Assertions.assertThrows(
                    IllegalArgumentException.class, () -> categoryService.deleteCategory(null));
            Assertions.assertEquals("CategoryId cannot be null", exception.getMessage());

            verify(categoryRepository, never()).deleteById(anyLong());
        }
    }
}
