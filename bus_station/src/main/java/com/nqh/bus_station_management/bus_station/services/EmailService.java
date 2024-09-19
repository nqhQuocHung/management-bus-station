package com.nqh.bus_station_management.bus_station.services;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
    void sendCargoTransportStatusEmail(String to, String companyName, boolean isCargoTransport);
}
