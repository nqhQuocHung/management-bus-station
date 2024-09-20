package com.nqh.bus_station_management.bus_station.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserChatDTO implements Serializable {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String avatar;

}
