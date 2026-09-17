package com.rikkeipay.config;

import io.langfuse.client.LangfuseClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình khởi tạo Bean LangfuseClient an toàn từ LangfuseProperties.
 * Ngăn chặn hoàn toàn việc hardcode credentials trong mã nguồn.
 */
@Configuration
@EnableConfigurationProperties(LangfuseProperties.class)
public class LangfuseConfig {

    private static final Logger log = LoggerFactory.getLogger(LangfuseConfig.class);

    private final LangfuseProperties properties;

    public LangfuseConfig(LangfuseProperties properties) {
        this.properties = properties;
    }

    @Bean
    public LangfuseClient langfuseClient() {
        log.info("[LangfuseConfig] Khởi tạo LangfuseClient kết nối tới Host: [{}] (Telemetry Enabled: {})",
                properties.getHost(), properties.isEnabled());

        if (properties.getPublicKey() == null || properties.getSecretKey() == null) {
            log.warn("[LangfuseConfig] CẢNH BÁO: Langfuse Public Key hoặc Secret Key chưa được cấu hình. Vui lòng kiểm tra biến môi trường!");
        }

        return new LangfuseClient(
                properties.getPublicKey(),
                properties.getSecretKey(),
                properties.getHost()
        );
    }
}
