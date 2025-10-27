package com.ecommerce.product_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Name of the product cannot be null")
    private String name;

    private String description;

    @NotNull(message = "Price Cannot be null")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Category Cannot be null")
    private String category;

    @NotNull(message = "Stock - quantity required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
}
