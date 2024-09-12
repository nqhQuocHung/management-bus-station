package com.busstation.controllers;

import com.busstation.services.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment-method")
public class ApiPaymentMethodController {

    @Autowired
    private PaymentMethodService service;

    @GetMapping("/list")
    private ResponseEntity<Object> list() {
        return ResponseEntity.ok(service.getAll());
    }
}
