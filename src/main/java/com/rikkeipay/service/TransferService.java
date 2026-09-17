package com.rikkeipay.service;

import com.rikkeipay.util.PiiMaskingUtils;
import io.langfuse.client.LangfuseClient;
import io.langfuse.client.model.Trace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service xử lý giao dịch chuyển tiền RikkeiPay tích hợp Langfuse Tracing an toàn.
 * Đảm bảo che giấu thông tin nhạy cảm (PII Masking), định danh Session/User tập trung
 * và xử lý ngoại lệ theo tiêu chuẩn hệ thống ngân hàng.
 */
@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final LangfuseClient langfuseClient;

    /**
     * Sử dụng Constructor Injection chuẩn mực của Spring Boot (thay thế @Autowired Field Injection).
     */
    public TransferService(LangfuseClient langfuseClient) {
        this.langfuseClient = langfuseClient;
    }

    /**
     * Xử lý giao dịch chuyển tiền với đầy đủ định danh truy vết và bảo mật dữ liệu PII.
     *
     * @param sessionId mã định danh phiên làm việc của khách hàng (Session Tracing)
     * @param userId mã định danh tài khoản khách hàng thực hiện giao dịch (User Tracing)
     * @param fromAccount số tài khoản trích nợ (nguồn)
     * @param toAccount số tài khoản thụ hưởng (đích)
     * @param amount số tiền giao dịch
     * @return transactionId mã định danh giao dịch ngân hàng
     */
    public String processTransfer(String sessionId, String userId, String fromAccount, String toAccount, double amount) {
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Instant startTime = Instant.now();

        // 1. Thực hiện PII Masking cho toàn bộ thông tin nhạy cảm trước khi log và gửi telemetry
        String maskedUser = PiiMaskingUtils.maskUserName(userId);
        String maskedFromAccount = PiiMaskingUtils.maskAccountNumber(fromAccount);
        String maskedToAccount = PiiMaskingUtils.maskAccountNumber(toAccount);
        String formattedAmount = PiiMaskingUtils.formatCurrency(amount);

        log.info("[TransferService] Bắt đầu xử lý giao dịch [{}] cho User [{}] từ TK [{}] sang TK [{}] - Số tiền: [{}]",
                transactionId, maskedUser, maskedFromAccount, maskedToAccount, formattedAmount);

        // 2. Khởi tạo Trace trên Langfuse với đầy đủ Session ID, User ID, Tags và Metadata bảo mật
        Trace trace = langfuseClient.trace(new Trace()
                .id(UUID.randomUUID().toString())
                .name("bank-transfer")
                .userId(userId)
                .sessionId(sessionId)
                .tags(List.of("financial-transaction", "transfer-service", "rikkeipay-core"))
                .metadata(Map.of(
                        "transactionId", transactionId,
                        "currency", "VND",
                        "environment", "production",
                        "initiatedAt", startTime.toString()
                ))
                .input(Map.of(
                        "action", "TRANSFER_MONEY",
                        "initiator", maskedUser,
                        "sourceAccountMasked", maskedFromAccount,
                        "destinationAccountMasked", maskedToAccount,
                        "amount", formattedAmount
                ))
        );

        try {
            // 3. Giả lập logic thực thi giao dịch ngân hàng cốt lõi (Core Banking Transaction)
            executeCoreBankingTransfer(fromAccount, toAccount, amount);

            // 4. Ghi nhận Output thành công lên Trace với dữ liệu đã được che giấu
            trace.output(Map.of(
                    "status", "SUCCESS",
                    "transactionId", transactionId,
                    "message", String.format("Chuyển thành công %s từ tài khoản %s sang tài khoản %s",
                            formattedAmount, maskedFromAccount, maskedToAccount),
                    "completedAt", Instant.now().toString()
            ));

            log.info("[TransferService] Giao dịch [{}] xử lý THÀNH CÔNG cho khách hàng [{}]", transactionId, maskedUser);
            return transactionId;

        } catch (Exception ex) {
            // 5. Bắt ngoại lệ, cập nhật trạng thái lỗi (ERROR level) lên Langfuse Trace để phục vụ cảnh báo giám sát
            log.error("[TransferService] Giao dịch [{}] THẤT BẠI. Lỗi: {}", transactionId, ex.getMessage(), ex);

            trace.output(Map.of(
                    "status", "FAILED",
                    "transactionId", transactionId,
                    "errorCode", "CORE_BANKING_ERROR",
                    "errorMessage", ex.getMessage(),
                    "failedAt", Instant.now().toString()
            ));

            // Lan truyền ngoại lệ cho tầng Controller xử lý phản hồi HTTP thích hợp
            throw new RuntimeException("Giao dịch chuyển tiền thất bại: " + ex.getMessage(), ex);
        }
    }

    /**
     * Logic nghiệp vụ cốt lõi trích nợ và ghi có vào tài khoản ngân hàng.
     */
    private void executeCoreBankingTransfer(String fromAccount, String toAccount, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền chuyển khoản phải lớn hơn 0");
        }
        if (fromAccount == null || toAccount == null || fromAccount.equals(toAccount)) {
            throw new IllegalArgumentException("Tài khoản thụ hưởng không hợp lệ hoặc trùng với tài khoản trích nợ");
        }
        // Giả lập xử lý thành công
    }
}
