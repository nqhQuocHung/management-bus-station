package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    @Override
    public void sendCargoTransportStatusEmail(String to, String companyName, boolean isCargoTransport) {
        String subject = "Thông báo cập nhật dịch vụ vận chuyển hàng hóa";
        String message = "<html><body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; color: #333;\">"
                + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); padding: 20px;\">"
                + "<h2 style=\"text-align: center; color: #007bff;\">Thông báo dịch vụ vận chuyển hàng hóa</h2>"
                + (isCargoTransport
                ? "<p>Chào " + companyName + ",</p><p style='font-size: 16px;'>Công ty của bạn đã đăng ký thành công dịch vụ vận chuyển hàng hóa.</p>"
                : "<p>Chào " + companyName + ",</p><p style='font-size: 16px;'>Công ty của bạn đã hủy dịch vụ vận chuyển hàng hóa.</p>")
                + "<p style=\"font-size: 16px; color: #555;\">Hãy liên hệ quản trị viên để biết thêm chi tiết.</p>"
                + "<p style=\"text-align: right; font-size: 16px; color: #555;\">Trân trọng,</p>"
                + "<p style=\"text-align: right; font-size: 16px; color: #007bff;\">Đội ngũ hỗ trợ</p>"
                + "</div>"
                + "<div style=\"text-align: center; padding: 10px; font-size: 12px; color: #777;\">"
                + "<p>Email này được gửi tự động, vui lòng không trả lời.</p>"
                + "</div>"
                + "</body></html>";

        sendHtmlEmail(to, subject, message);
    }

    @Override
    public void sendRegistrationSuccessEmail(String to, String firstName, String lastName, String username, String password) {
        String subject = "Đăng ký thành công";
        String fullName = lastName + " " + firstName;
        String message = "<html><body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; color: #333;\">"
                + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); padding: 20px;\">"
                + "<h2 style=\"text-align: center; color: #007bff;\">Đăng ký thành công</h2>"
                + "<p>Chào " + fullName + ",</p>"
                + "<p>Bạn đã đăng ký thành công. Vui lòng chờ quản trị viên xác nhận để có thể đăng nhập.</p>"
                + "<p>Thông tin tài khoản của bạn:</p>"
                + "<ul style='font-size: 16px;'>"
                + "<li><strong>Tên đăng nhập:</strong> " + username + "</li>"
                + "<li><strong>Mật khẩu:</strong> " + password + "</li>"
                + "</ul>"
                + "<p>Vui lòng không chia sẻ thông tin này với bất kỳ ai.</p>"
                + "<p>Bạn sẽ nhận được thông báo khi tài khoản được xác nhận.</p>"
                + "<p style=\"text-align: right;\">Trân trọng,<br>Đội ngũ hỗ trợ của chúng tôi</p>"
                + "</div>"
                + "<div style=\"text-align: center; padding: 10px; font-size: 12px; color: #777;\">"
                + "<p>Email này được gửi tự động, vui lòng không trả lời.</p>"
                + "</div></body></html>";

        sendHtmlEmail(to, subject, message);
    }

    @Override
    public void sendDriverApprovalEmail(String to, String lastName, String firstName) {
        String driverName = lastName + " " + firstName;
        String subject = "Chúc mừng! Đơn đăng ký tài xế đã được phê duyệt";
        String message = "<html><body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; color: #333;\">"
                + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); padding: 20px;\">"
                + "<h2 style=\"text-align: center; color: #28a745;\">Đơn đăng ký tài xế được phê duyệt</h2>"
                + "<p>Xin chào " + driverName + ",</p>"
                + "<p>Chúng tôi vui mừng thông báo rằng đơn đăng ký tài xế của bạn đã được phê duyệt. Chúc mừng bạn đã trở thành tài xế của chúng tôi!</p>"
                + "<p style=\"text-align: right;\">Trân trọng,<br>Đội ngũ hỗ trợ</p>"
                + "</div>"
                + "<div style=\"text-align: center; padding: 10px; font-size: 12px; color: #777;\">"
                + "<p>Email này được gửi tự động, vui lòng không trả lời.</p>"
                + "</div></body></html>";

        sendHtmlEmail(to, subject, message);
    }

    @Override
    public void sendDriverSuspensionEmail(String to, String lastName, String firstName) {
        String driverName = lastName + " " + firstName;
        String subject = "Thông báo: Tạm ngưng công việc";
        String message = "<html><body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; color: #333;\">"
                + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); padding: 20px;\">"
                + "<h2 style=\"text-align: center; color: #dc3545;\">Thông báo tạm ngưng công việc</h2>"
                + "<p>Xin chào " + driverName + ",</p>"
                + "<p>Chúng tôi rất tiếc phải thông báo rằng bạn đã bị tạm ngưng công việc. Vui lòng liên hệ với bộ phận hỗ trợ để biết thêm thông tin.</p>"
                + "<p style=\"text-align: right;\">Trân trọng,<br>Đội ngũ hỗ trợ</p>"
                + "</div>"
                + "<div style=\"text-align: center; padding: 10px; font-size: 12px; color: #777;\">"
                + "<p>Email này được gửi tự động, vui lòng không trả lời.</p>"
                + "</div></body></html>";

        sendHtmlEmail(to, subject, message);
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email", e);
        }
    }


}
