package com.nqh.bus_station_management.bus_station.services;

import com.nqh.bus_station_management.bus_station.pojo.PaymentMethod;
import java.util.List;

public interface PaymentMethodService {
    List<PaymentMethod> getAllPaymentMethods();
}
