package com.eyc.key.modules.kyc.service;

import com.eyc.key.modules.kyc.entity.KycSubmission;
import com.eyc.key.modules.kyc.enums.KycStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycEmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // Email cần lấy từ UserRepository — inject qua constructor
    // Để đơn giản, nhận email trực tiếp từ caller

    @Async
    public void sendReviewResultEmail(KycSubmission submission) {
        // Cần lấy email của user — gọi UserRepository ở đây
        // Tạm thời log, implement sau khi có UserRepository inject
        log.info("TODO: Send KYC result email for submission: {}, status: {}",
                submission.getId(), submission.getStatus());
    }

    @Async
    public void sendKycResultEmail(String toEmail, String fullName,
                                   KycStatus status, String rejectNote) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper  helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(getSubject(status));
            helper.setText(buildContent(fullName, status, rejectNote), true);

            mailSender.send(message);
            log.info("KYC result email sent to {}, status: {}", toEmail, status);
        } catch (MessagingException e) {
            log.error("Failed to send KYC email: {}", e.getMessage());
        }
    }

    private String getSubject(KycStatus status) {
        return switch (status) {
            case APPROVED -> "[eKYC] Hồ sơ định danh của bạn đã được duyệt";
            case REJECTED -> "[eKYC] Hồ sơ định danh của bạn bị từ chối";
            case RESUBMIT_REQUIRED -> "[eKYC] Yêu cầu bổ sung hồ sơ định danh";
            default -> "[eKYC] Cập nhật trạng thái hồ sơ";
        };
    }

    private String buildContent(String fullName, KycStatus status, String note) {
        String body = switch (status) {
            case APPROVED -> """
                    <p>Chúc mừng! Hồ sơ định danh của bạn đã được <strong style="color:#16a34a;">xét duyệt thành công</strong>.</p>
                    <p>Bạn có thể đăng nhập và tiến hành mở tài khoản ngân hàng.</p>
                    """;
            case REJECTED -> """
                    <p>Rất tiếc, hồ sơ định danh của bạn đã bị <strong style="color:#dc2626;">từ chối</strong>.</p>
                    <p><strong>Lý do:</strong> %s</p>
                    """.formatted(note != null ? note : "Vui lòng liên hệ hỗ trợ");
            case RESUBMIT_REQUIRED -> """
                    <p>Hồ sơ của bạn cần được <strong style="color:#d97706;">bổ sung</strong>.</p>
                    <p><strong>Yêu cầu:</strong> %s</p>
                    <p>Vui lòng đăng nhập và nộp lại hồ sơ.</p>
                    """.formatted(note != null ? note : "Vui lòng cung cấp thêm thông tin");
            default -> "<p>Hồ sơ của bạn đang được xử lý.</p>";
        };

        return """
                <div style="font-family: Arial, sans-serif; max-width: 500px; margin: 0 auto;">
                    <h2 style="color: #1a56db;">Thông báo kết quả KYC</h2>
                    <p>Xin chào <strong>%s</strong>,</p>
                    %s
                    <hr/>
                    <p style="color: #6b7280; font-size: 12px;">Email tự động từ hệ thống eKYC.</p>
                </div>
                """.formatted(fullName, body);
    }
}
