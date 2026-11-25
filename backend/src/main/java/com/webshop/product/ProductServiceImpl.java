package com.webshop.product;

import com.webshop.category.Category;
import com.webshop.category.CategoryRepository;
import com.webshop.category.CategoryStatus;
import com.webshop.common.exceptions.BadRequestException;
import com.webshop.common.exceptions.ResourceConflictException;
import com.webshop.common.exceptions.ResourceNotFoundException;
import com.webshop.common.persistence.ConstraintViolations;
import com.webshop.common.util.SafePageables;
import com.webshop.common.util.TextNormalize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "price");
    private static final String NAME_CONSTRAINT = "uk_products_name";
    private static final Set<String> ALLOWED_ADMIN_SORT_FIELDS =
            Set.of("id", "name", "price", "stock", "status", "createdAt", "updatedAt");
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private static void requireValidPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new BadRequestException("minPrice must not be greater than maxPrice");
        }
    }

    private static RuntimeException translateNameConflict(DataIntegrityViolationException e, String name) {
        return ConstraintViolations.isViolationOf(e, NAME_CONSTRAINT)
                ? new ResourceConflictException("Product with name = " + name + " already exists")
                : e;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(ProductFilterDto filter, Pageable pageable) {
        if (filter != null) {
            requireValidPriceRange(filter.minPrice(), filter.maxPrice());
        }
        Specification<Product> spec = ProductSpecification.forCatalog(filter);
        Pageable safe = SafePageables.withSafeSort(pageable, ALLOWED_SORT_FIELDS, Sort.by("id"));
        return productRepository.findAll(spec, safe).map(ProductMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ProductAdminResponse> adminSearch(AdminProductFilter filter, Pageable pageable) {
        if (filter != null) {
            requireValidPriceRange(filter.minPrice(), filter.maxPrice());
        }
        Specification<Product> spec = ProductSpecification.forAdmin(filter);
        Pageable safe = SafePageables.withSafeSort(pageable, ALLOWED_ADMIN_SORT_FIELDS,
                Sort.by(Sort.Direction.DESC, "updatedAt"));
        return productRepository.findAll(spec, safe).map(ProductMapper::toAdminResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public ProductAdminResponse getProductForAdmin(Long productId) {
        return ProductMapper.toAdminResponse(findAnyProductOrThrow(productId));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse addProduct(ProductCreateRequest request) {
        if (request == null) {
            throw new BadRequestException("Product request is required");
        }

        String name = request.name();
        if (productRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceConflictException("Product with name = " + name + " already exists");
        }

        Category category = findActiveCategory(request.categoryId());
        Product product = new Product(name, request.description(), request.price(), request.stock(), category);

        try {
            Product saved = productRepository.saveAndFlush(product);
            log.info("Product created: id={}, categoryId={}", saved.getId(), category.getId());
            return ProductMapper.toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw translateNameConflict(e, name);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {
        if (request == null) {
            throw new BadRequestException("Product request is required");
        }

        String name = request.name();
        if (productRepository.existsByNameIgnoreCaseAndIdNot(name, productId)) {
            throw new ResourceConflictException("Product with name = " + name + " already exists");
        }

        Product existing = findActiveProductOrThrow(productId);
        existing.rename(name);
        existing.changeDescription(request.description());
        existing.changePrice(request.price());
        existing.changeCategory(findActiveCategory(request.categoryId()));

        try {
            productRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw translateNameConflict(e, name);
        }

        log.info("Product updated: id={}", productId);
        return ProductMapper.toResponse(existing);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse adjustStock(Long productId, int delta) {
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        int before = product.getStock();
        product.adjustStock(delta);

        log.info("Stock adjusted: productId={}, delta={}, before={}, after={}",
                productId, delta, before, product.getStock());
        return ProductMapper.toResponse(product);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse activateProduct(Long productId) {
        Product product = findAnyProductOrThrow(productId);

        product.activate();
        log.info("Product activated: id={}", productId);
        return ProductMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        return ProductMapper.toResponse(findActiveProductOrThrow(productId));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(Long productId) {
        Product product = findActiveProductOrThrow(productId);
        product.archive();
        log.info("Product archived: id={}", productId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameIgnoreCase(String name) {
        return productRepository.existsByNameIgnoreCase(TextNormalize.normalize(name));
    }

    private Product findActiveProductOrThrow(Long productId) {
        return productRepository.findByIdAndStatus(productId, ProductStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }

    private Category findActiveCategory(Long categoryId) {
        return categoryRepository.findByIdAndStatus(categoryId, CategoryStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
    }

    private Product findAnyProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }
}