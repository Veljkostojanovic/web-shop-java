package com.webshop.product;

import com.webshop.category.Category;
import com.webshop.category.CategoryRepository;
import com.webshop.common.exceptions.BadRequestException;
import com.webshop.common.exceptions.ResourceConflictException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "price");

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> search(ProductFilterDto filter, Pageable pageable) {
        validateSort(pageable);
        Specification<Product> spec = ProductSpecification.buildSpecification(filter);
        return productRepository.findAll(spec, pageable).map(ProductMapper::toDTO);
    }

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

    private Pageable validateSort(Pageable pageable){
        for(Sort.Order order : pageable.getSort()){
            if(!ALLOWED_SORT_FIELDS.contains(order.getProperty())){
                throw new BadRequestException("Invalid sort field: " + order.getProperty());
            }
        }
        return pageable;
    }
}
