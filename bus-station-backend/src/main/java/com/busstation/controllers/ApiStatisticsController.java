package com.busstation.controllers;

import com.busstation.dtos.StatisticsDTO;
import com.busstation.services.TicketService;
import com.busstation.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/statistics")
public class ApiStatisticsController {
    @Autowired
    TicketService ticketService;


    @GetMapping("year/{year}")
    public Map<Integer, StatisticsDTO> getAnnualRevenue(
            @PathVariable(name = "year") Integer year,
            @RequestParam(name = "companyId") Long companyId) {
        return ticketService.getAnnualRevenue(year, companyId);
    }

    @GetMapping("/quarterly/{year}")
    public Map<Integer, StatisticsDTO> getQuarterlyRevenue(
            @PathVariable(name = "year") Integer year,
            @RequestParam(name = "companyId") Long companyId) {
        return ticketService.getQuarterlyRevenue(year, companyId);
    }

    @GetMapping("/day/{year}/{month}/{day}")
    public Map<Integer, StatisticsDTO> getDailyRevenue(
            @PathVariable(name = "year") Integer year,
            @PathVariable(name = "month") Integer month,
            @PathVariable(name = "day") Integer day,
            @RequestParam(name = "companyId") Long companyId) {
        return ticketService.getDailyRevenue(year, month, day, companyId);
    }

}
