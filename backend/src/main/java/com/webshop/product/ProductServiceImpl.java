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
        if (productRepository.existsByNameIgnoreCase(productDTO.getName())) {
            throw new ResourceConflictException("Product name already exists");
        }

        if(productDTO.getCategory() == null || productDTO.getCategory().getId() == null){
            throw new BadRequestException("Product must have category");
        }

        Long categoryId = productDTO.getCategory().getId();
        Category category = categoryRepository.findById(categoryId).
                orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        Product product = ProductMapper.toEntity(productDTO);
        product.setCategory(category);

        Product saved = productRepository.save(product);
        return ProductMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
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
        Product product = productRepository.findById(productId)
                            .orElseThrow( () -> new ResourceNotFoundException("Product id not found"));

        return ProductMapper.toDTO(product);
    }

    @Override
    @Transactional
    public boolean deleteProduct(Long productId) {
        if(!productRepository.existsById(productId)){
            throw new ResourceNotFoundException("Product id not found");
        }
        productRepository.deleteById(productId);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameIgnoreCase(String name) {
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
