package com.busstation.controllers;

import com.busstation.dtos.TransportationCompanyDTO;
import com.busstation.pojo.Station;
import com.busstation.pojo.TransportationCompany;
import com.busstation.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.busstation.pojo.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transportation_company")
public class ApiTransportationCompanyController {

    @Autowired
    private TransportationCompanyService transportationCompanyService;

    @Autowired
    private UserService userService;

    @Autowired
    private StationService stationService;

    @Autowired
    private CloudinaryService cloudinaryService;


    @GetMapping("/list")
    public ResponseEntity<Object> list(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(transportationCompanyService.list(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransportationCompany> getTransportationCompanyById(@PathVariable Long id) {
        return transportationCompanyService.getTransportationCompanyById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/new")
    public ResponseEntity<TransportationCompany> createTransportationCompany(@RequestBody TransportationCompany transportationCompany) {
        TransportationCompany newCompany = transportationCompanyService.saveTransportationCompany(transportationCompany);
        return ResponseEntity.ok(newCompany);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransportationCompany(@PathVariable Long id) {
        transportationCompanyService.deleteTransportationCompany(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransportationCompany(
            @PathVariable Long id,
            @RequestPart("company") TransportationCompanyDTO dto,
            @RequestPart(value = "avatar", required = false) MultipartFile avatarFile) {
            String avatarUrl;
            try {
                avatarUrl = cloudinaryService.uploadFile(avatarFile);
            } catch (IOException e) {
                return ResponseEntity.badRequest().build();
            }
            User user = userService.getUserById(dto.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            TransportationCompany transportationCompany = transportationCompanyService.getTransportationCompanyById(id)
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            transportationCompany.setPhone(dto.getPhone());
            transportationCompany.setManager(user);
            transportationCompany.setAvatar(avatarUrl);
            transportationCompany.setName(dto.getName());
            transportationCompany.setEmail(dto.getEmail());
            transportationCompany.setIsVerified(dto.getIsVerified());
            transportationCompany.setIsCargoTransport(dto.getIsCargoTransport());
            transportationCompany.setIsActive(dto.getIsActive());

            transportationCompanyService.updateTransportationCompany(transportationCompany);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/list/name-and-id")
    public ResponseEntity<Object> listNameAndId(){
        return ResponseEntity.ok(transportationCompanyService.getAllNameAndId());
    }

    @PostMapping("/add")
    public ResponseEntity<TransportationCompany> createTransportationCompany(
            @RequestPart("company") TransportationCompanyDTO dto,
            @RequestPart("avatar") MultipartFile avatarFile) {
        String avatarUrl;
        try {
            avatarUrl = cloudinaryService.uploadFile(avatarFile);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }

        User manager = userService.getUserById(dto.getManagerId())
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        TransportationCompany company = TransportationCompany.builder()
                .name(dto.getName())
                .avatar(avatarUrl)
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .isVerified(dto.getIsVerified())
                .isActive(dto.getIsActive())
                .isCargoTransport(dto.getIsCargoTransport())
                .manager(manager)
                .build();

        TransportationCompany newCompany = transportationCompanyService.saveTransportationCompany(company);
        return ResponseEntity.ok(newCompany);
    }

    @GetMapping("/unverified")
    public ResponseEntity<List<TransportationCompany>> getUnverifiedCompanies() {
        List<TransportationCompany> unverifiedCompanies = transportationCompanyService.getUnverifiedCompanies();
        return ResponseEntity.ok(unverifiedCompanies);
    }

    @PutMapping("/verify/{id}")
    public ResponseEntity<?> verifyCompany(@PathVariable Long id) {
        transportationCompanyService.verifyCompany(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/cargo/{id}")
    public ResponseEntity<?> cargo(@PathVariable Long id) {
        transportationCompanyService.cargo(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("get/{id}")
    public ResponseEntity<TransportationCompanyDTO> getCompanyAndManager(@PathVariable Long id) {
        TransportationCompanyDTO companyDto = transportationCompanyService.getCompanyAndManager(id);
        if (companyDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(companyDto);
    }
    @GetMapping("/list_station")
    public ResponseEntity<List<Station>> getAllStations() {
        List<Station> stations = stationService.getAllStations();
        return ResponseEntity.ok(stations);
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<TransportationCompanyDTO> getCompanyByManagerId(@PathVariable Long managerId) {
        TransportationCompanyDTO company = transportationCompanyService.getCompanyByManagerId(managerId);
        if (company == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(company);
    }

    @GetMapping("/count/user/{id}")
    public long getUserCountByRoleId(@PathVariable Long id) {
        return userService.getUserCountByRoleId(id);
    }
    @GetMapping("/count/company")
    public long getTotalCompanyCount() {
        return transportationCompanyService.getTotalCompanyCount();
    }

}