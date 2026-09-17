package com.vietmart.order_service.exception;

/**
 * Ngoại lệ nghiệp vụ khi sản phẩm không tồn tại trong hệ thống (HTTP 404).
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }
}
