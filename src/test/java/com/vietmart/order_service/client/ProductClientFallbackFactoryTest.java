package com.vietmart.order_service.client;

import com.vietmart.order_service.dto.ProductInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductClientFallbackFactoryTest {

    private ProductClientFallbackFactory fallbackFactory;

    @BeforeEach
    void setUp() {
        fallbackFactory = new ProductClientFallbackFactory();
    }

    @Test
    @DisplayName("Unit Test 1: Fallback getById - Trả về ProductInfo dự phòng an toàn khi gặp sự cố")
    void testFallback_GetById_ReturnsFallbackProductInfo() {
        // Arrange
        Throwable cause = new SocketTimeoutException("Read timed out after 3000ms");
        ProductClient fallbackClient = fallbackFactory.create(cause);
        Long productId = 505L;

        // Act
        ProductInfo result = fallbackClient.getById(productId);

        // Assert
        assertNotNull(result, "Fallback ProductInfo không được null");
        assertEquals(productId, result.getId());
        assertEquals("Sản phẩm tạm thời không khả dụng (Fallback)", result.getName());
        assertEquals(BigDecimal.ZERO, result.getPrice());
        assertEquals(0, result.getStockQuantity());
        assertEquals("UNAVAILABLE", result.getStatus());
        assertTrue(result.getDescription().contains("gián đoạn"), "Description phải giải thích lý do gián đoạn");
    }

    @Test
    @DisplayName("Unit Test 2: Fallback getAll - Trả về danh sách rỗng khi gặp sự cố, bảo vệ Order Service")
    void testFallback_GetAll_ReturnsEmptyList() {
        // Arrange
        Throwable cause = new RuntimeException("503 Service Unavailable");
        ProductClient fallbackClient = fallbackFactory.create(cause);

        // Act
        List<ProductInfo> products = fallbackClient.getAll();

        // Assert
        assertNotNull(products, "Danh sách trả về không được null");
        assertTrue(products.isEmpty(), "Danh sách trả về phải rỗng khi fallback kích hoạt");
    }

    @Test
    @DisplayName("Unit Test 3: Fallback Factory tiếp nhận Throwable mà không gây Crash")
    void testFallbackFactory_HandlesCauseGracefully() {
        // Arrange
        Throwable cause = new IllegalStateException("Connection refused to product-service");

        // Act
        ProductClient fallbackClient = fallbackFactory.create(cause);

        // Assert
        assertNotNull(fallbackClient, "FallbackClient proxy không được null");
        ProductInfo product = fallbackClient.getById(999L);
        assertEquals(999L, product.getId());
    }
}
