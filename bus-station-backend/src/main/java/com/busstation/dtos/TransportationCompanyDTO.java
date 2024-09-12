package com.busstation.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportationCompanyDTO {
    private Long id;
    private String name;
    private String avatar;
    private String phone;
    private String email;
    private Boolean isVerified;
    private Boolean isActive;
    private Boolean isCargoTransport;
    private Long managerId;
}
