package com.busstation.repositories;

import com.busstation.pojo.Seat;

public interface SeatRepository {
    Seat getById(Long id);
}
