package com.vietmart.order_service.client;

import com.vietmart.order_service.dto.ProductInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * FallbackFactory cho ProductClient:
 * - Tiếp nhận Throwable nguyên nhân gây lỗi (Timeout, 503 Service Unavailable, lỗi mạng I/O,...).
 * - Log chi tiết exception phục vụ giám sát và khắc phục sự cố.
 * - Trả về dữ liệu fallback an toàn:
 *   + getById(id): Trả về đối tượng ProductInfo dự phòng với status "UNAVAILABLE"
 *   + getAll(): Trả về danh sách rỗng để không làm crash luồng nghiệp vụ của Order Service.
 */
@Component
@Slf4j
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {

    @Override
    public ProductClient create(Throwable cause) {
        log.error("ProductClient fallback kích hoạt do lỗi: [{}], chi tiết: {}",
                cause.getClass().getSimpleName(), cause.getMessage(), cause);

        return new ProductClient() {
            @Override
            public ProductInfo getById(Long id) {
                log.warn("Kích hoạt fallback cho getById(id={}). Nguyên nhân: {}", id, cause.getMessage());
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

            @Override
            public List<ProductInfo> getAll() {
                log.warn("Kích hoạt fallback cho getAll(). Nguyên nhân: {}. Trả về danh sách rỗng.", cause.getMessage());
                return Collections.emptyList();
            }
        };
    }
}
