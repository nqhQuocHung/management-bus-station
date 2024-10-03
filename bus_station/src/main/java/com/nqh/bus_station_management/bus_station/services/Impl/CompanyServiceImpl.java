package com.nqh.bus_station_management.bus_station.services.Impl;

import com.nqh.bus_station_management.bus_station.dtos.CompanyDTO;
import com.nqh.bus_station_management.bus_station.dtos.CompanyPublicDTO;
import com.nqh.bus_station_management.bus_station.dtos.CompanyRegisterDTO;
import com.nqh.bus_station_management.bus_station.pojo.TransportationCompany;
import com.nqh.bus_station_management.bus_station.pojo.User;
import com.nqh.bus_station_management.bus_station.repositories.CompanyRepository;
import com.nqh.bus_station_management.bus_station.repositories.UserRepository;
import com.nqh.bus_station_management.bus_station.services.CompanyService;
import com.nqh.bus_station_management.bus_station.services.EmailService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private Environment environment;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Map<String, Object> listCompanies(Map<String, String> params) {
        String name = params.get("name");
        int page = params.get("page") != null ? Integer.parseInt(params.get("page")) : 1;
        int pageSize = Integer.parseInt(environment.getProperty("company.pageSize", "10"));
        List<TransportationCompany> companies = companyRepository.list(name);
        Long totalCompanies = companyRepository.count(name);

        int totalPages = (int) Math.ceil((double) totalCompanies / pageSize);
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, companies.size());

        List<TransportationCompany> paginatedCompanies = companies.subList(startIndex, endIndex);

        Map<String, Object> response = new HashMap<>();
        response.put("results", paginatedCompanies);
        response.put("total", totalCompanies);
        response.put("pageTotal", totalPages);

        return response;
    }

    @Override
    public Map<String, Object> listVerifiedCompanies(Map<String, String> params) {
        String name = params.get("name");
        int page = params.get("page") != null ? Integer.parseInt(params.get("page")) : 1;
        int pageSize = Integer.parseInt(environment.getProperty("company.pageSize", "10"));
        List<TransportationCompany> companies = companyRepository.getListVerified(name);
        int totalCompanies = companies.size();

        int totalPages = (int) Math.ceil((double) totalCompanies / pageSize);
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalCompanies);
        List<TransportationCompany> paginatedCompanies = companies.subList(startIndex, endIndex);
        Map<String, Object> response = new HashMap<>();
        response.put("results", paginatedCompanies);
        response.put("total", totalCompanies);
        response.put("pageTotal", totalPages);

        return response;
    }

    @Override
    public Long countCompanies(Map<String, String> params) {
        String name = params.get("name");
        return companyRepository.count(name);
    }

    @Override
    public Optional<TransportationCompany> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    @Override
    public TransportationCompany createCompany(CompanyRegisterDTO companyDTO) {
        User manager = userRepository.findById(companyDTO.getManagerId())
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        TransportationCompany company = TransportationCompany.builder()
                .name(companyDTO.getName())
                .avatar(companyDTO.getAvatar())
                .phone(companyDTO.getPhone())
                .email(companyDTO.getEmail())
                .isCargoTransport(companyDTO.getIsCargoTransport())
                .manager(manager) // Cài đặt manager
                .isVerified(false)
                .isActive(true)
                .build();

        return companyRepository.save(company);
    }



    @Override
    public void deleteCompanyById(Long id) {
        companyRepository.deleteById(id);
    }

    @Override
    public void updateCompany(CompanyDTO companyDTO) {
        Optional<TransportationCompany> companyOptional = companyRepository.findById(companyDTO.getId());
        if (companyOptional.isPresent()) {
            TransportationCompany company = companyOptional.get();
            company.setName(companyDTO.getName());
            company.setAvatar(companyDTO.getAvatar());
            company.setPhone(companyDTO.getPhone());
            company.setEmail(companyDTO.getEmail());
            company.setIsVerified(companyDTO.getIsVerified());
            company.setIsActive(companyDTO.getIsActive());
            company.setIsCargoTransport(companyDTO.getIsCargoTransport());

            if (companyDTO.getManagerId() != null) {
                Optional<User> manager = userRepository.findById(companyDTO.getManagerId());
                manager.ifPresent(company::setManager);
            }

            companyRepository.save(company);
        }
    }

    @Override
    public List<CompanyDTO> getUnverifiedCompanies() {
        List<TransportationCompany> companies = companyRepository.findByIsVerifiedFalse();
        return companies.stream()
                .map(company -> new CompanyDTO(
                        company.getId(),
                        company.getName(),
                        company.getAvatar(),
                        company.getPhone(),
                        company.getEmail(),
                        company.getIsVerified(),
                        company.getIsActive(),
                        company.getIsCargoTransport(),
                        company.getManager().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void verifyCompany(Long id) {
        Optional<TransportationCompany> companyOptional = companyRepository.findById(id);

        if (companyOptional.isPresent()) {
            TransportationCompany company = companyOptional.get();
            boolean newVerifiedStatus = !company.getIsVerified();
            company.setIsVerified(newVerifiedStatus);

            companyRepository.save(company);

            String emailTo = company.getEmail();
            String subject = newVerifiedStatus
                    ? "Công ty của bạn đã được xác thực thành công"
                    : "Xác thực của công ty bạn đã bị hủy";

            String htmlContent = newVerifiedStatus
                    ? String.format(
                    "<html><body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; color: #333;\">"
                            + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); padding: 20px;\">"
                            + "<h2 style=\"color: #28a745; text-align: center;\">Xác thực thành công</h2>"
                            + "<p>Xin chào %s,</p>"
                            + "<p>Công ty <strong>%s</strong> của bạn đã được xác thực thành công. Bạn có thể truy cập hệ thống để quản lý các dịch vụ của mình.</p>"
                            + "<p style=\"text-align: right;\">Trân trọng,<br>Đội ngũ hỗ trợ Bus Station</p>"
                            + "</div>"
                            + "<div style=\"text-align: center; padding: 10px; font-size: 12px; color: #777;\">"
                            + "<p>Email này được gửi tự động, vui lòng không trả lời.</p>"
                            + "</div></body></html>",
                    company.getManager().getFirstname() + " " + company.getManager().getLastname(),
                    company.getName()
            )
                    : String.format(
                    "<html><body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; color: #333;\">"
                            + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); padding: 20px;\">"
                            + "<h2 style=\"color: #dc3545; text-align: center;\">Xác thực bị hủy bỏ</h2>"
                            + "<p>Xin chào %s,</p>"
                            + "<p>Xác thực của công ty <strong>%s</strong> đã bị hủy bỏ. Nếu bạn có thắc mắc, vui lòng liên hệ đội ngũ hỗ trợ để biết thêm chi tiết.</p>"
                            + "<p style=\"text-align: right;\">Trân trọng,<br>Đội ngũ hỗ trợ Bus Station</p>"
                            + "</div>"
                            + "<div style=\"text-align: center; padding: 10px; font-size: 12px; color: #777;\">"
                            + "<p>Email này được gửi tự động, vui lòng không trả lời.</p>"
                            + "</div></body></html>",
                    company.getManager().getFirstname() + " " + company.getManager().getLastname(),
                    company.getName()
            );
            emailService.sendHtmlEmail(emailTo, subject, htmlContent);
        } else {
            throw new EntityNotFoundException("Không tìm thấy công ty với ID: " + id);
        }
    }



    @Override
    public CompanyDTO getCompanyAndManager(Long companyId) {
        return companyRepository.getCompanyAndManager(companyId);
    }

    @Override
    public TransportationCompany findByManagerId(Long id) {
        return companyRepository.findByManagerId(id);
    }

    @Override
    public long countAllCompanies() {
        return companyRepository.countAllCompanies();
    }

    @Override
    public List<CompanyPublicDTO> getNameId() {
        return companyRepository.findAll().stream()
                .map(company -> CompanyPublicDTO.builder()
                        .id(company.getId())
                        .name(company.getName())
                        .build())
                .collect(Collectors.toList());
    }

    public void toggleCargoTransport(Long companyId) {
        TransportationCompany company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        company.setIsCargoTransport(!company.getIsCargoTransport());
        companyRepository.save(company);

        emailService.sendCargoTransportStatusEmail(
                company.getEmail(),
                company.getName(),
                company.getIsCargoTransport()
        );
    }

    @Override
    public long getVerifiedCompanyCount() {
        return companyRepository.countVerifiedCompanies();
    }

    @Override
    public long getActiveCompanyCount() {
        return companyRepository.countActiveCompanies();
    }

    @Override
    public List<CompanyDTO> getAllCompanies() {
        List<TransportationCompany> companies = companyRepository.findAll();
        return companies.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private CompanyDTO convertToDTO(TransportationCompany company) {
        return CompanyDTO.builder()
                .id(company.getId())
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
}
