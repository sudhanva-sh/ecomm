package com.ecommerce.product_service.dto;

import com.ecommerce.product_service.entity.Product;

public class ProductMapper {

    public static Product toEntity(ProductRequest productRequest){
        return Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .category(productRequest.getCategory())
                .price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .build();
    }

    public static ProductResponse toResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .stock(product.getStock())
                .price(product.getPrice())
                .build();
    }
}
