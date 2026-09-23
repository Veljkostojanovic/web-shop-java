package com.webshop.product;

import com.webshop.category.Category;
import com.webshop.category.CategoryRepository;
import com.webshop.common.exceptions.BadRequestException;
import com.webshop.common.exceptions.ResourceConflictException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "price");

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(ProductFilterDto filter, Pageable pageable) {
        log.info("Searching for products filter = {}, pageable = {}", filter, pageable);
        validateSort(pageable);
        Specification<Product> spec = ProductSpecification.buildSpecification(filter);
        return productRepository.findAll(spec, pageable).map(ProductMapper::toDTO);
    }

    @Override
    @Transactional
    public ProductResponse addProduct(ProductCreateRequest productCreateRequest) {
        log.info("Adding product productName = {}, categoryId = {}",  productCreateRequest.name(), productCreateRequest.categoryId());
        if (productRepository.existsByNameIgnoreCase(productCreateRequest.name())) {
            throw new ResourceConflictException("Product with name = " + productCreateRequest.name() + " already exists");
        }

        Long categoryId =  productCreateRequest.categoryId();
        Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        Product product = ProductMapper.toEntity(productCreateRequest);
        product.setCategory(category);

        Product saved = productRepository.save(product);
        log.info("Added product productId = {}, categoryId = {}", product.getId(), product.getCategory().getId());
        return ProductMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest productUpdateRequest) {
        log.info("Updating product productId = {}", productId);
        Product existing = productRepository.findById(productId)
                .orElseThrow( () -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if(productRepository.existsByNameIgnoreCaseAndIdNot(productUpdateRequest.name(), productId)){
            throw new ResourceConflictException("Product with name = " + productUpdateRequest.name() + " already exists");
        }

        existing.setName(productUpdateRequest.name());
        existing.setPrice(productUpdateRequest.price());
        existing.setDescription(productUpdateRequest.description());
        existing.setStock(productUpdateRequest.stock());

        if (productUpdateRequest.categoryId() != null) {
            existing.setCategory(categoryRepository.findById(productUpdateRequest.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productUpdateRequest.categoryId())));
        }

        log.info("Updated product productId = {}", productId);
        return ProductMapper.toDTO(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                            .orElseThrow( () -> new ResourceNotFoundException("Product not found with ID: " + productId));

        return ProductMapper.toDTO(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        log.info("Deleting product productId = {}", productId);
        if(!productRepository.existsById(productId)){
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }
        productRepository.deleteById(productId);

        log.info("Deleted Product productId = {}", productId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameIgnoreCase(String name) {
        return productRepository.existsByNameIgnoreCase(name);
    }

    private void validateSort(Pageable pageable){
        for(Sort.Order order : pageable.getSort()){
            if(!ALLOWED_SORT_FIELDS.contains(order.getProperty())){
                throw new BadRequestException("Invalid sort field: " + order.getProperty());
            }
        }
    }
}
