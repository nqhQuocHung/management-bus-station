package com.nqh.bus_station_management.bus_station.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest implements Serializable {
    @NotNull(message = "{user.username.notNull}")
    @Size(min = 5, max = 255, message = "{user.username.size}")
    private String username;
    @NotNull(message = "{user.email.notNull}")
    @Size(min = 5, max = 255, message = "{user.email.size}")
    private String email;
    @NotNull(message = "{user.password.notNull}")
    @Size(min = 3, max = 255, message = "{user.password.size}")
    private String password;
    @NotNull(message = "{user.role.notNull}")
    @Size(min = 5, max =20, message = "{user.role.notNull}")
    private String role;
}
