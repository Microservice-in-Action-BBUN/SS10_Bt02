package com.rikkeipay.util;

/**
 * Tiện ích hỗ trợ che giấu và mã hóa dữ liệu nhạy cảm của khách hàng (PII Masking)
 * tuân thủ các tiêu chuẩn an toàn thông tin tài chính - ngân hàng (PCI-DSS, ISO 27001, GDPR).
 */
public final class PiiMaskingUtils {

    private PiiMaskingUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Che giấu số tài khoản ngân hàng (Giữ lại 4 ký tự đầu và 3 ký tự cuối).
     * Ví dụ: "1903456789" -> "1903****789"
     *
     * @param accountNumber số tài khoản gốc
     * @return số tài khoản đã được mask
     */
    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            return "****";
        }
        String clean = accountNumber.trim();
        if (clean.length() <= 6) {
            return clean.charAt(0) + "****" + clean.charAt(clean.length() - 1);
        }
        int prefixLen = 4;
        int suffixLen = 3;
        int maskLen = clean.length() - prefixLen - suffixLen;
        return clean.substring(0, prefixLen) + "*".repeat(Math.max(3, maskLen)) + clean.substring(clean.length() - suffixLen);
    }

    /**
     * Che giấu tên khách hàng hoặc định danh người dùng.
     * Ví dụ: "Nguyen Van A" -> "N*** A", "user12345" -> "u***5"
     *
     * @param userName tên định danh khách hàng
     * @return tên định danh đã được mask
     */
    public static String maskUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            return "Anonymous";
        }
        String clean = userName.trim();
        String[] parts = clean.split("\\s+");
        if (parts.length > 1) {
            StringBuilder masked = new StringBuilder();
            masked.append(parts[0].charAt(0)).append("*** ");
            masked.append(parts[parts.length - 1]);
            return masked.toString();
        }
        if (clean.length() <= 2) {
            return clean.charAt(0) + "*";
        }
        return clean.charAt(0) + "***" + clean.charAt(clean.length() - 1);
    }

    /**
     * Định dạng số tiền giao dịch kèm đơn vị tiền tệ an toàn.
     *
     * @param amount số tiền giao dịch
     * @return chuỗi biểu diễn số tiền chuẩn hóa
     */
    public static String formatCurrency(double amount) {
        return String.format("%,.0f VND", amount);
    }
}
