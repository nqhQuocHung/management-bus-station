package com.busstation.services.impl;

import com.busstation.dtos.CompanyPublicDTO;
import com.busstation.dtos.TransportationCompanyDTO;
import com.busstation.mappers.CompanyPublicMapper;
import com.busstation.pojo.TransportationCompany;
import com.busstation.repositories.TransportationCompanyRepository;
import com.busstation.services.EmailService;
import com.busstation.services.TransportationCompanyService;
import com.busstation.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@PropertySource("classpath:configuration.properties")
public class TransportationCompanyServiceImpl implements TransportationCompanyService {

    @Autowired
    private Environment environment;


    @Autowired
    private TransportationCompanyRepository repository;

    @Autowired
    private CompanyPublicMapper companyPublicMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;



    @Override
    public Map<String, Object> list(Map<String, String> params) {
        List<TransportationCompany> results = repository.list(params);
        Long total = repository.count(params);
        int pageSize = Integer.parseInt(environment.getProperty("transportationCompany.pageSize"));
        int pageTotal = (int) ((total / pageSize) + 1);
        Map<String, Object> m = new HashMap<>();
        m.put("total", total);
        m.put("pageTotal", pageTotal);
        m.put("results", results);
        return m;

    }

    @Override
    public List<CompanyPublicDTO> getAllNameAndId() {
        List<TransportationCompany> results = repository.list(new HashMap<>());
        List<CompanyPublicDTO> r = results.stream().map(companyPublicMapper::apply).collect(Collectors.toList());
        return r;
    }

    @Override
    public Optional<TransportationCompany> getTransportationCompanyById(Long id) {
        return Optional.ofNullable(repository.getTransportationCompanyById(id));
    }

    @Override
    public TransportationCompany saveTransportationCompany(TransportationCompany transportationCompany) {
        repository.saveTransportationCompany(transportationCompany);
        return transportationCompany;
    }

    @Transactional
    @Override
    public void updateTransportationCompany(TransportationCompany transportationCompany) {

    }

    @Override
    @Transactional
    public void deleteTransportationCompany(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public List<TransportationCompany> getUnverifiedCompanies() {
        return repository.findByIsVerifiedFalse();
    }

    @Override
    @Transactional
    public void verifyCompany(Long id) {
        repository.verifyCompany(id);
        Optional<TransportationCompany> companyOpt = repository.findById(id);
        userService.changeRole(companyOpt.get().getManager().getId(), (long)3);
        if (companyOpt.isPresent()) {
            TransportationCompany company = companyOpt.get();
            String to = company.getEmail();
            String subject = "Your Company has been Verified";
            String text = "Dear " + company.getName() + ",\n\nYour company has been successfully verified.\n\nBest regards,\nBus Station Team";
            emailService.sendEmail(to, subject, text);
        }
    }

    @Override
    @Transactional
    public void cargo(Long id) {
        repository.cargo(id);
        Optional<TransportationCompany> companyOpt = repository.findById(id);
        if (companyOpt.isPresent()) {
            TransportationCompany company = companyOpt.get();
            String to = company.getEmail();
            String subject = "Đăng kí giao hàng thành công";
            String text = "Chào " + company.getName() + ",\n\nCông ty của bạn đã đăng ký giao hàng thành công.\n\nChúc mừng!\nBus Station Team";
            emailService.sendEmail(to, subject, text);
        }
    }

    @Override
    public TransportationCompanyDTO getCompanyAndManager(Long companyId) {
        return repository.getCompanyAndManager(companyId);
    }

    @Override
    public TransportationCompanyDTO getCompanyByManagerId(Long managerId) {
        TransportationCompany company = repository.findByManagerId(managerId);
        return convertToDTO(company);
    }

    private TransportationCompanyDTO convertToDTO(TransportationCompany company) {
        if (company == null) {
            return null;
        }
        return TransportationCompanyDTO.builder()
                .id((company.getId()))
                .name(company.getName())
                .avatar(company.getAvatar())
                .phone(company.getPhone())
                .email(company.getEmail())
                .isVerified(company.getIsVerified())
                .isActive(company.getIsActive())
                .isCargoTransport(company.getIsCargoTransport())
                .managerId(company.getManager().getId())
                .build();
    }

    @Override
    public long getTotalCompanyCount() {
        return repository.countAllCompanies();
    }
}