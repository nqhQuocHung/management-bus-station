package com.busstation.dtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CompanyPublicDTO {

    private String name;
    private Long id;
}
