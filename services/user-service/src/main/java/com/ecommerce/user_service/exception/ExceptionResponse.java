package com.ecommerce.user_service.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionResponse {
    private String message;
}
