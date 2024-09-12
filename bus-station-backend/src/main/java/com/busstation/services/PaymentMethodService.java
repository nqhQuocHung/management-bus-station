package com.busstation.services;


import com.busstation.pojo.PaymentMethod;

import java.util.List;

public interface PaymentMethodService {
    List<PaymentMethod> getAll();
}
