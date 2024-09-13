package com.nqh.bus_station_management.bus_station.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginWithGoogleRequest implements Serializable {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String avatar;
}
