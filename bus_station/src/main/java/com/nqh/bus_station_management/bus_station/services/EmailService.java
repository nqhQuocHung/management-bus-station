package com.nqh.bus_station_management.bus_station.services;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
    void sendCargoTransportStatusEmail(String to, String companyName, boolean isCargoTransport);
    void sendRegistrationSuccessEmail(String to, String firstName, String lastName, String username, String password);
    void sendDriverApprovalEmail(String to, String lastName, String firstName);
    void sendDriverSuspensionEmail(String to, String lastName, String firstName);
    void sendHtmlEmail(String to, String subject, String htmlContent);
    void sendOtpEmail(String to, String otp);
}
