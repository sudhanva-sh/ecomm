package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.ProductRequest;
import com.ecommerce.product_service.dto.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse updateProduct(Long id, ProductRequest productRequest);
    void deleteProduct(Long id);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    Page<ProductResponse> getPagedProducts(int page, int size, String sortBy, String sortOrder);
    Page<ProductResponse> searchProducts(String keyword, String category,
                                         Double minPrice, Double maxPrice,
                                         int page, int size, String sortBy, String sortDir);

}
