package com.vietmart.order_service.client;

import com.vietmart.order_service.dto.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Interface FeignClient giao tiếp với user-service để lấy thông tin người mua hàng:
 * - Khai báo phương thức declarative getUserById(Long userId).
 */
@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/{userId}")
    UserInfo getUserById(@PathVariable("userId") Long userId);
}
