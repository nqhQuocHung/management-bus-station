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
        String message = isCargoTransport
                ? "<p>Chào " + companyName + ",</p><p>Công ty của bạn đã đăng ký thành công dịch vụ vận chuyển hàng hóa.</p>"
                : "<p>Chào " + companyName + ",</p><p>Công ty của bạn đã hủy dịch vụ vận chuyển hàng hóa.</p>";

        message += "<p>Hãy liên hệ quản trị viên để biết thêm chi tiết.</p><p>Trân trọng!</p>";

        sendHtmlEmail(to, subject, message);
    }


    @Override
    public void sendRegistrationSuccessEmail(String to, String firstName, String lastName, String username, String password) {
        String subject = "Đăng ký thành công";
        String fullName = lastName + " " + firstName;
        String message = "<p>Chào " + fullName + ",</p>" +
                "<p>Bạn đã đăng ký thành công. Vui lòng chờ quản trị viên xác nhận để có thể đăng nhập.</p>" +
                "<p>Thông tin tài khoản của bạn như sau:</p>" +
                "<ul>" +
                "<li><strong>Tên đăng nhập:</strong> " + username + "</li>" +
                "<li><strong>Mật khẩu:</strong> " + password + "</li>" +
                "</ul>" +
                "<p>Vui lòng không chia sẻ thông tin này với bất kỳ ai.</p>" +
                "<p>Bạn sẽ nhận được thông báo qua email khi tài khoản của bạn được xác nhận.</p>" +
                "<p>Trân trọng,<br>Đội ngũ hỗ trợ của chúng tôi.</p>";

        sendHtmlEmail(to, subject, message);
    }


    @Override
    public void sendDriverApprovalEmail(String to, String lastName, String firstName) {
        String driverName = lastName + " " + firstName;
        String subject = "Chúc mừng! Đơn đăng ký tài xế đã được phê duyệt";
        String message = "<p>Xin chào " + driverName + ",</p>" +
                "<p>Chúng tôi vui mừng thông báo rằng đơn đăng ký tài xế của bạn đã được phê duyệt. Chúc mừng bạn đã trở thành tài xế của chúng tôi!</p>" +
                "<p>Trân trọng,<br>Đội ngũ hỗ trợ</p>";

        sendHtmlEmail(to, subject, message);
    }

    @Override
    public void sendDriverSuspensionEmail(String to, String lastName, String firstName) {
        String driverName = lastName + " " + firstName;
        String subject = "Thông báo: Tạm ngưng công việc";
        String message = "<p>Xin chào " + driverName + ",</p>" +
                "<p>Chúng tôi rất tiếc phải thông báo rằng bạn đã bị tạm ngưng công việc. Vui lòng liên hệ với bộ phận hỗ trợ để biết thêm thông tin.</p>" +
                "<p>Trân trọng,<br>Đội ngũ hỗ trợ</p>";

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
