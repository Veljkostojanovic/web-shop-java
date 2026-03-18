package com.webshop.product;

import com.webshop.category.Category;
import com.webshop.category.CategoryRepository;
import com.webshop.common.exceptions.ResourceConflictException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductDTO addProduct(ProductDTO productDTO) {
        if (productDTO == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        if (productRepository.existsByNameIgnoreCase(productDTO.getName())) {
            throw new ResourceConflictException("Product name already exists");
        }

        Product product = ProductMapper.toEntity(productDTO);

        if (productDTO.getCategory() != null && productDTO.getCategory().getId() != null) {
            Long catId = productDTO.getCategory().getId();
            Category category = categoryRepository.findById(catId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + catId));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }

        Product saved = productRepository.save(product);
        return ProductMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        if(productDTO == null)throw new IllegalArgumentException("Product cannot be null");
        if(productId == null)throw new IllegalArgumentException("Product id cannot be null");


        Product existing = productRepository.findById(productId)
                .orElseThrow( () -> new ResourceNotFoundException("Product id not found"));

        existing.setName(productDTO.getName());
        existing.setPrice(productDTO.getPrice());
        existing.setDescription(productDTO.getDescription());
        existing.setStock(productDTO.getStock());

        if (productDTO.getCategory() != null && productDTO.getCategory().getId() != null) {
            existing.setCategory(categoryRepository.findById(productDTO.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
        }

        Product updated = productRepository.save(existing);

        return ProductMapper.toDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long productId) {
        if(productId == null){
            throw new  IllegalArgumentException("Product id cannot be null");
        }

        Product product = productRepository.findById(productId)
                            .orElseThrow( () -> new ResourceNotFoundException("Product id not found"));

        return ProductMapper.toDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findByCategoryId(Long categoryId) {
        if(categoryId == null){
            throw new  IllegalArgumentException("Category id cannot be null");
        }

        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findByNameContainingIgnoreCase(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteProduct(Long productId) {
        if(productId == null){
            throw new IllegalArgumentException("Product id cannot be null");
        }

        if(!productRepository.existsById(productId)){
            throw new ResourceNotFoundException("Product id not found");
        }
        productRepository.deleteById(productId);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameIgnoreCase(String name) {
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        return productRepository.existsByNameIgnoreCase(name);
    }
}
