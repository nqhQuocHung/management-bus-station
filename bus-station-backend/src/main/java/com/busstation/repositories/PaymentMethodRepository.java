package com.busstation.repositories;

import com.busstation.pojo.PaymentMethod;

import java.util.List;

public interface PaymentMethodRepository {
    List<PaymentMethod> getAll();

    PaymentMethod getById(Long id);
}
