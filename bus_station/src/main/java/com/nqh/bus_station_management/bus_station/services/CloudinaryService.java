package com.nqh.bus_station_management.bus_station.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {

    String uploadImage(MultipartFile file) throws IOException;

}
