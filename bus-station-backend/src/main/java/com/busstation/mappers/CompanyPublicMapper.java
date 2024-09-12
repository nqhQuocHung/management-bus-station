package com.busstation.mappers;

import com.busstation.dtos.CompanyPublicDTO;
import com.busstation.pojo.TransportationCompany;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CompanyPublicMapper implements Function<TransportationCompany, CompanyPublicDTO> {
    @Override
    public CompanyPublicDTO apply(TransportationCompany transportationCompany) {
        return CompanyPublicDTO.builder()
                .id(transportationCompany.getId())
                .name(transportationCompany.getName())
                .build();
    }
}
