package com.busstation.repositories;
import com.busstation.pojo.Car;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CarRepository {
    List<Car> findAvailableCarsByDate(Date date);
    Car getById(Long id);
    Optional<Car> findById(Long id);
}
