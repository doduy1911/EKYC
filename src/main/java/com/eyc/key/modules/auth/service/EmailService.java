package com.eyc.key.modules.auth.service;

import com.eyc.key.common.enums.OtpType;
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
public class EmailService {
    private final  JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;
    @Value("${otp.expiration-minutes:5}")
    private int otpExpirationMinutes;

//    @Async
    public void sendOtpEmail(String toEmail, String fullName , String otp , OtpType type) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(getSubject(type));
            helper.setText(buildHtmlContent(fullName, otp , type), true);
            mailSender.send(message);
            log.info("OTP email sent to {}", toEmail);


        }catch (Exception e){
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Không Thể gửi mail OTP");
        }
    }

    private String getSubject(OtpType type) {
        return  switch (type){
            case REGISTER -> "[eKYC] Mã xác thực đăng ký tài khoản";
            case RESET_PASSWORD -> "[eKYC] Mã xác thực đặt lại mật khẩu";
        };
    }

    private String buildHtmlContent(String fullName, String otp,
                                    OtpType type) {
        String action = switch (type) {
            case REGISTER -> "đăng ký tài khoản";
            case RESET_PASSWORD -> "đặt lại mật khẩu";
        };

        return """
                <div style="font-family: Arial, sans-serif; max-width: 500px; margin: 0 auto;">
                    <h2 style="color: #1a56db;">Xác thực %s</h2>
                    <p>Xin chào <strong>%s</strong>,</p>
                    <p>Mã OTP của bạn là:</p>
                    <div style="background: #f3f4f6; padding: 20px; text-align: center;
                                border-radius: 8px; margin: 20px 0;">
                        <span style="font-size: 36px; font-weight: bold;
                                     letter-spacing: 8px; color: #1a56db;">%s</span>
                    </div>
                    <p>Mã có hiệu lực trong <strong>%d phút</strong>.</p>
                    <p style="color: #ef4444;">Không chia sẻ mã này với bất kỳ ai.</p>
                </div>
                """.formatted(action, fullName, otp, otpExpirationMinutes);
    };
}




