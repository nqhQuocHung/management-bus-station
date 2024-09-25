package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.pojo.OnlinePaymentResult;
import com.nqh.bus_station_management.bus_station.repositories.OnlinePaymentResultRepository;
import com.nqh.bus_station_management.bus_station.services.OnlinePaymentResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OnlinePaymentResultServiceImpl implements OnlinePaymentResultService {

    @Autowired
    private  OnlinePaymentResultRepository paymentResultRepository;


    @Override
    public OnlinePaymentResult createPaymentResult(OnlinePaymentResult paymentResult) {
        return paymentResultRepository.save(paymentResult);
    }
}
