package com.busstation.services;

import com.busstation.dtos.CarPublicDTO;
import java.util.Date;
import java.util.List;

public interface CarService {
    List<CarPublicDTO> getAvailableCarsByDate(Date date);
}
