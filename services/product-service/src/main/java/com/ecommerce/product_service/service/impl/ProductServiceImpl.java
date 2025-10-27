package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.dto.ProductMapper;
import com.ecommerce.product_service.dto.ProductRequest;
import com.ecommerce.product_service.dto.ProductResponse;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.exception.ProductNotFoundException;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;


    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product saved =  productRepository.save(ProductMapper.toEntity(productRequest));

        return ProductMapper.toResponse(saved);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id not found: "+id));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());

        return ProductMapper.toResponse(
                productRepository.save(product)
        );
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toResponse)
                .toList();

    }

    @Override
    public ProductResponse getProductById(Long id) {
        return ProductMapper.toResponse(
                productRepository.findById(id).orElseThrow(()->new ProductNotFoundException("Product with id not found: "+id))
        );
    }

    @Override
    public Page<ProductResponse> getPagedProducts(int page,
                                                  int size,
                                                  String sortBy,
                                                  String sortOrder){
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();

        Pageable pagableProducts = PageRequest.of(page, size, sort);

        return productRepository.findAll(pagableProducts)
                .map(ProductMapper::toResponse);
    }

    @Override
    public Page<ProductResponse> searchProducts(String keyword, String category, Double minPrice, Double maxPrice, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        System.out.println("keyword "+ keyword );
        Page<Product> productPage = productRepository.searchProducts(keyword, category, minPrice, maxPrice, pageable);

        return productPage.map(ProductMapper::toResponse);
    }
}
