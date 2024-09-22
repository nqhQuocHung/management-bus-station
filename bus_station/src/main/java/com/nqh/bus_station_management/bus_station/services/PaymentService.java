package com.nqh.bus_station_management.bus_station.services;

import java.util.Map;

public interface PaymentService {
    String handleVnpayReturn(Map<String, String> params);
    String createPaymentUrl(long amount, String orderInfo) throws Exception;
}
