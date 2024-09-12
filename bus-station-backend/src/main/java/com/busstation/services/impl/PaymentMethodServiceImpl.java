package com.busstation.services.impl;

import com.busstation.pojo.PaymentMethod;
import com.busstation.repositories.PaymentMethodRepository;
import com.busstation.services.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Override
    public List<PaymentMethod> getAll() {
        return paymentMethodRepository.getAll();
    }
}
