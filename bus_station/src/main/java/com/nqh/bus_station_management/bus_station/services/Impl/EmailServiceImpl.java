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

}
