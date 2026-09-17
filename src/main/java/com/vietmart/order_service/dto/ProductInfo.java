package com.vietmart.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) chứa thông tin sản phẩm nhận từ product-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInfo {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
    private String description;
    private String category;
    private String status;

    /**
     * Helper method tạo dữ liệu fallback an toàn cho ProductInfo.
     */
    public static ProductInfo fallback(Long id) {
        return ProductInfo.builder()
                .id(id)
                .name("Sản phẩm tạm thời không khả dụng (Fallback)")
                .price(BigDecimal.ZERO)
                .stockQuantity(0)
                .description("Thông tin sản phẩm tạm thời gián đoạn do sự cố kết nối tới product-service.")
                .category("UNKNOWN")
                .status("UNAVAILABLE")
                .build();
    }
}
