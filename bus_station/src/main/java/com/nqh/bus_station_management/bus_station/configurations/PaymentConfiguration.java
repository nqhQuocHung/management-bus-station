package com.nqh.bus_station_management.bus_station.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@Data
public class PaymentConfiguration {

    @Value("${vnpay.tmnCode:YIWM3W6K}")
    private String vnpTmnCode;

    @Value("${vnpay.hashSecret:24SBIMYKXLX9YNGTF2GR34I6HFQHP6CN4}")
    private String vnpHashSecret;

    @Value("${vnpay.url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpUrl;

    @Value("${vnpay.returnUrl:http://localhost:8080/api/payment/vnpay_return}")
    private String vnpReturnUrl;
}
