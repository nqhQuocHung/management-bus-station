package com.busstation.services;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}
