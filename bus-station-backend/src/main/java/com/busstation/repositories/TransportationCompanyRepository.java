package com.busstation.repositories;

import com.busstation.dtos.TransportationCompanyDTO;
import com.busstation.pojo.TransportationCompany;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TransportationCompanyRepository {

    List<TransportationCompany> list(Map<String,String> params) ;
    Long count(Map<String, String> params);

    TransportationCompany getTransportationCompanyById(Long id);
    void saveTransportationCompany(TransportationCompany newtransportationCompany);
    void deleteById(Long id);
    void updateTransportationCompany(TransportationCompany transportationCompany);
    List<TransportationCompany> findByIsVerifiedFalse();
    Optional<TransportationCompany> findById(Long id);
    void save(TransportationCompany company);
    void verifyCompany(Long id);
    TransportationCompanyDTO getCompanyAndManager(Long companyId);
    TransportationCompany findByManagerId(Long id);
    void cargo(Long id);
    long countAllCompanies();
}