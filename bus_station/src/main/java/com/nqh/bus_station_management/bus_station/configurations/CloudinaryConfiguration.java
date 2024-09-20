package com.nqh.bus_station_management.bus_station.configurations;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfiguration {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dxxwcby8l",
                "api_key", "448651448423589",
                "api_secret", "ftGud0r1TTqp0CGp5tjwNmkAm-A",
                "secure", true
        ));
    }
}
