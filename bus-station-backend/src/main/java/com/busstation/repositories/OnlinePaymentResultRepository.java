package com.busstation.repositories;

import com.busstation.pojo.OnlinePaymentResult;

public interface OnlinePaymentResultRepository {

    OnlinePaymentResult getById(Long id);

    OnlinePaymentResult getByPaymentCode(String paymentCode);

    void update(OnlinePaymentResult paymentResult);
}
