package com.rikkeipay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình tập trung thông số kết nối Langfuse Server sử dụng @ConfigurationProperties.
 * Đảm bảo không hardcode API key và hỗ trợ linh hoạt các môi trường qua biến môi trường.
 */
@Configuration
@ConfigurationProperties(prefix = "langfuse")
public class LangfuseProperties {

    /**
     * Public Key của dự án trên Langfuse (dùng cho Ingestion)
     */
    private String publicKey;

    /**
     * Secret Key của dự án trên Langfuse (bảo mật tuyệt đối, không commit lên git)
     */
    private String secretKey;

    /**
     * Địa chỉ URL của Langfuse Server (Self-host local hoặc Cloud)
     */
    private String host = "http://localhost:3000";

    /**
     * Trạng thái kích hoạt gửi dữ liệu Telemetry/Tracing lên Langfuse
     */
    private boolean enabled = true;

    // Getters and Setters
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
