package com.nqh.bus_station_management.bus_station.controllers;

import com.nqh.bus_station_management.bus_station.pojo.OnlinePaymentResult;
import com.nqh.bus_station_management.bus_station.services.OnlinePaymentResultService;
import com.nqh.bus_station_management.bus_station.services.PaymentService;
import com.nqh.bus_station_management.bus_station.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private  PaymentService paymentService;

    @Autowired
    private  OnlinePaymentResultService paymentResultService;

    @Autowired
    private  TicketService ticketService;

    @PostMapping("/create")
    public ResponseEntity<?> createPayment() {
        try {
            long fixedAmount = 250000;
            String fixedOrderInfo = "Order for user Khai Nguyen Van";

            String encodedOrderInfo = URLEncoder.encode(fixedOrderInfo, StandardCharsets.UTF_8.toString());

            String paymentUrl = paymentService.createPaymentUrl(fixedAmount, encodedOrderInfo);

            System.out.println("Fixed Amount: " + fixedAmount);
            System.out.println("Encoded Order Info: " + encodedOrderInfo);

            return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error creating payment URL", "details", e.getMessage()));
        }
    }



    @GetMapping("/vnpay_return")
    public ResponseEntity<?> handleVnpayReturn(@RequestParam Map<String, String> params, @RequestParam Long ticketId, @RequestParam Long paymentMethodId) {
        String responseCode = params.get("vnp_ResponseCode");

        if ("00".equals(responseCode)) {
            OnlinePaymentResult paymentResult = OnlinePaymentResult.builder()
                    .paymentCode(params.get("vnp_TmnCode"))
                    .bankCode(params.get("vnp_BankCode"))
                    .transactionNo(params.get("vnp_TransactionNo"))
                    .bankTransactionNo(params.get("vnp_BankTranNo"))
                    .cardType(params.get("vnp_CardType"))
                    .confirmAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            try {
                OnlinePaymentResult savedPaymentResult = paymentResultService.createPaymentResult(paymentResult);
                ticketService.updateTicketPayment(ticketId, savedPaymentResult.getId(), paymentMethodId);
                return ResponseEntity.ok(Map.of("message", "Payment successful", "transactionNumber", params.get("vnp_TransactionNo")));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Error processing payment", "details", e.getMessage()));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Payment failed", "responseCode", responseCode));
        }
    }

}
