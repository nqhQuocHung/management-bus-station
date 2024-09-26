package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
                ? "Chào " + companyName + ",\n\nCông ty của bạn đã đăng ký thành công dịch vụ vận chuyển hàng hóa."
                : "Chào " + companyName + ",\n\nCông ty của bạn đã hủy dịch vụ vận chuyển hàng hóa.";

        message += "\n\nHãy liên hệ quản trị viên để biết thêm chi tiết.\nTrân trọng!";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }

    @Override
    public void sendRegistrationSuccessEmail(String to, String firstName, String lastName, String username, String password) {
        String subject = "Đăng ký thành công";
        String fullName = lastName + " " + firstName;
        String text = "Chào " + fullName + ",\n\n"
                + "Bạn đã đăng ký thành công. Vui lòng chờ quản trị viên xác nhận để có thể đăng nhập.\n"
                + "Thông tin tài khoản của bạn như sau:\n"
                + "Tên đăng nhập: " + username + "\n"
                + "Mật khẩu: " + password + "\n\n"
                + "Vui lòng không chia sẻ thông tin này với bất kỳ ai.\n\n"
                + "Bạn sẽ nhận được thông báo qua email khi tài khoản của bạn được xác nhận.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ hỗ trợ của chúng tôi.";
        sendEmail(to, subject, text);
    }

    @Override
    public void sendDriverApprovalEmail(String to, String lastName, String firstName) {
        String driverName = lastName + " " + firstName;
        String subject = "Chúc mừng! Đơn đăng ký tài xế đã được phê duyệt";
        String text = "Xin chào " + driverName + ",\n\n" +
                "Chúng tôi vui mừng thông báo rằng đơn đăng ký tài xế của bạn đã được phê duyệt. " +
                "Chúc mừng bạn đã trở thành tài xế của chúng tôi!\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ hỗ trợ";
        sendEmail(to, subject, text);
    }

    @Override
    public void sendDriverSuspensionEmail(String to, String lastName, String firstName) {
        String driverName = lastName + " " + firstName;
        String subject = "Thông báo: Tạm ngưng công việc";
        String text = "Xin chào " + driverName + ",\n\n" +
                "Chúng tôi rất tiếc phải thông báo rằng bạn đã bị tạm ngưng công việc. " +
                "Vui lòng liên hệ với bộ phận hỗ trợ để biết thêm thông tin.\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ hỗ trợ";
        sendEmail(to, subject, text);
    }
}
