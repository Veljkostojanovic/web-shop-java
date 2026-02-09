package com.webshop.product;

import com.webshop.category.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }


    @Override
    @Transactional
    public ProductDTO addProduct(ProductDTO productDTO) {
        if(productDTO == null){
            throw new IllegalArgumentException("product cannot be null");
        }

        Product product = ProductMapper.toEntity(productDTO);

        if(productDTO.getCategory().getId() != null){
            product.setCategory(categoryRepository.
                    findById(productDTO.getCategory().getId()).orElseThrow( () -> new IllegalArgumentException("category id not found")));
        }

        Product saved = productRepository.save(product);
        return ProductMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        if(productDTO == null)throw new IllegalArgumentException("product cannot be null");
        if(productId == null)throw new IllegalArgumentException("productId cannot be null");


        Product existing = productRepository.findById(productId)
                .orElseThrow( () -> new IllegalArgumentException("product id not found"));

        existing.setName(productDTO.getName());
        existing.setPrice(productDTO.getPrice());
        existing.setDescription(productDTO.getDescription());
        existing.setStock(productDTO.getStock());

        if (productDTO.getCategory() != null && productDTO.getCategory().getId() != null) {
            existing.setCategory(categoryRepository.findById(productDTO.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found")));
        }

        Product updated = productRepository.save(existing);

        return ProductMapper.toDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long productId) {
        if(productId == null){
            throw new  IllegalArgumentException("productId cannot be null");
        }

        Product product =  productRepository.findById(productId)
                            .orElseThrow( () -> new IllegalArgumentException("product id not found"));

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
            throw new  IllegalArgumentException("categoryId cannot be null");
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
            throw new IllegalArgumentException("productId cannot be null");
        }

        if(!productRepository.existsById(productId)){
            return false;
        }
        productRepository.deleteById(productId);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameIgnoreCase(String name) {
        if(name == null || name.isEmpty()){
            throw new  IllegalArgumentException("name cannot be null or empty");
        }

        return productRepository.existsByNameIgnoreCase(name);
    }
}
