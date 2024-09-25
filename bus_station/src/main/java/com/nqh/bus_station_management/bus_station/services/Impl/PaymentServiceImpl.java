package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.configurations.PaymentConfiguration;
import com.nqh.bus_station_management.bus_station.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentConfiguration paymentConfig;

    @Autowired
    public PaymentServiceImpl(PaymentConfiguration paymentConfig) {
        this.paymentConfig = paymentConfig;
    }

    @Override
    public String createPaymentUrl(long amount, String orderInfo) throws Exception {
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", paymentConfig.getVnpTmnCode());
        vnpParams.put("vnp_Amount", String.valueOf(amount * 100));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", String.valueOf(new Date().getTime()));
        vnpParams.put("vnp_OrderInfo", URLEncoder.encode(orderInfo, StandardCharsets.UTF_8.toString()));
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", paymentConfig.getVnpReturnUrl());
//        vnpParams.put("vnp_ReturnUrl", URLEncoder.encode(paymentConfig.getVnpReturnUrl(), StandardCharsets.UTF_8.toString()));

        String ipAddr = InetAddress.getLocalHost().getHostAddress();
        String hashedIpAddr = hmacSHA512(paymentConfig.getVnpHashSecret(), ipAddr);
        vnpParams.put("vnp_IpAddr", URLEncoder.encode(hashedIpAddr, StandardCharsets.UTF_8.toString()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        vnpParams.put("vnp_CreateDate", dateFormat.format(calendar.getTime()));

        calendar.add(Calendar.MINUTE, 15);
        vnpParams.put("vnp_ExpireDate", dateFormat.format(calendar.getTime()));

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = vnpParams.get(fieldName);
            if (value != null && !value.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
                query.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
                if (!fieldName.equals(fieldNames.get(fieldNames.size() - 1))) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String vnpSecureHash = hmacSHA512(paymentConfig.getVnpHashSecret(), hashData.toString());
        query.append("&vnp_SecureHash=").append(vnpSecureHash);

        return paymentConfig.getVnpUrl() + "?" + query.toString();
    }

    private String hmacSHA512(String key, String data) throws Exception {
        Mac hmac512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac512.init(secretKey);
        byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }

    @Override
    public String handleVnpayReturn(Map<String, String> params) {
        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo = params.get("vnp_TransactionNo");

        if ("00".equals(responseCode)) {
            return "Payment successful with transaction number: " + transactionNo;
        } else {
            return "Payment failed!";
        }
    }

    @Override
    public boolean validateSignature(Map<String, String> params, String secureHash) {
        params.remove("vnp_SecureHash");

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = params.get(fieldName);
            if ((value != null) && (value.length() > 0)) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8));
                hashData.append('&');
            }
        }

        String data = hashData.length() > 0 ? hashData.substring(0, hashData.length() - 1) : "";

        try {
            String calculatedHash = hmacSHA512(paymentConfig.getVnpHashSecret(), data);
            return calculatedHash.equals(secureHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
