package com.busstation.services;


import com.busstation.dtos.CheckoutResponse;
import com.busstation.dtos.StatisticsDTO;
import com.busstation.dtos.TicketDTO;
import com.busstation.pojo.Ticket;
import com.busstation.pojo.User;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface TicketService {


    List<TicketDTO> getInfoFromCart(List<Map<String, String>> clientCart);

    List<Ticket> createTickets(List<Map<String, String>> client_cart, Long payment_method_id);

    CheckoutResponse checkout(List<Map<String, String>> client_cart, Long paymentMethodId, String ip) throws UnsupportedEncodingException;

    String handleVnPayResponse(Map<String, String> params) throws ParseException;

    Map<Integer, StatisticsDTO> getAnnualRevenue(int year, Long companyId);

    Map<Integer, StatisticsDTO> getQuarterlyRevenue(int year, Long companyId);

    Map<Integer, StatisticsDTO> getDailyRevenue(int year, int month, int day, Long companyId);

    List<TicketDTO> getTicketByUserId(Long userId);

    void delete(Long id);

    boolean hasObjectPermission(Ticket ticket, User user );

}
