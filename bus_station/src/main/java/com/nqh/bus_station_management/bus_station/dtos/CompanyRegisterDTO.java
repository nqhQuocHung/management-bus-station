package com.nqh.bus_station_management.bus_station.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRegisterDTO {

    @NotBlank(message = "Tên công ty không được để trống")
    @Size(max = 255, message = "Tên công ty không được dài hơn 255 ký tự")
    private String name;

    @NotBlank(message = "Avatar không được để trống")
    private String avatar;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 50, message = "Số điện thoại không được dài hơn 50 ký tự")
    private String phone;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    private Boolean isCargoTransport;

    private Long managerId;  // Thêm trường này để gửi id của người quản lý
}

