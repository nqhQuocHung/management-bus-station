package com.busstation.services.impl;

import com.busstation.dtos.*;
import com.busstation.mappers.TicketDTOMapper;
import com.busstation.pojo.*;
import com.busstation.repositories.*;
import com.busstation.services.RouteService;
import com.busstation.services.TicketService;
import com.busstation.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.criteria.Root;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@PropertySource("classpath:vnpay.properties")
public class TicketServiceImpl implements TicketService {

    @Autowired
    private RouteService routeService;

    @Autowired
    private TripService tripService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;


    @Autowired
    private TicketDTOMapper ticketDTOMapper;

    @Autowired
    private Environment environment;

    @Autowired
    private OnlinePaymentResultRepository paymentResultRepository;



    @Override
    public List<TicketDTO> getInfoFromCart(List<Map<String, String>> clientCart) {
        List<TicketDTO> results = new ArrayList<>();
        clientCart.forEach(c -> {
            try {
                Long routeId = Long.parseLong(c.get("routeId"));
                Long tripId = Long.parseLong(c.get("tripId"));
                Long seatId = Long.parseLong(c.get("seatId"));

                Boolean withCargo = Boolean.valueOf(c.get("withCargo"));

                RouteDTO routeInfo = routeService.getById(routeId);
                if (!withCargo.booleanValue()) {
                    routeInfo.setCargoPrice(0.0);
                }
                TripDTO tripInfo = tripService.tripInfo(tripId);
                Seat seat = seatRepository.getById(seatId);
                TicketDTO newTicket = TicketDTO.builder()
                        .routeInfo(routeInfo)
                        .tripInfo(tripInfo)
                        .seat(seat)
                        .build();
                results.add(newTicket);
            } catch (NullPointerException e) {
                System.out.println(e.getMessage());
            }
        });
        return results;
    }

    @Override
    public List<Ticket> createTickets(List<Map<String, String>> client_cart, Long paymentMethodId) {
        List<Ticket> tickets = new ArrayList<>();
        PaymentMethod paymentMethod = paymentMethodRepository.getById(paymentMethodId);
        client_cart.forEach(c -> {
            Long tripId = Long.parseLong(c.get("tripId"));
            Long seatId = Long.parseLong(c.get("seatId"));
            Boolean withCargo = Boolean.valueOf(c.get("withCargo"));
            Seat thisSeat = tripService.availableSeat(tripId, seatId);
            System.out.println(thisSeat.getId());
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Trip thisTrip = tripService.getById(tripId);
            Ticket thisTicket = Ticket.builder()
                    .trip(thisTrip)
                    .customer(user)
                    .seat(thisSeat)
                    .seatPrice(thisTrip.getRoute().getSeatPrice())
                    .paymentMethod(paymentMethod)
                    .build();
            if (withCargo.booleanValue()) {
                Cargo thisCargo = new Cargo();
                thisCargo.setCargoPrice(thisTrip.getRoute().getCargoPrice());
                thisCargo.setTicket(thisTicket);
                thisTicket.setCargo(thisCargo);
            }
            tickets.add(thisTicket);

        });

        return tickets;
    }

    @Override
    public CheckoutResponse checkout(List<Map<String, String>> client_cart, Long paymentMethodId, String ip) throws UnsupportedEncodingException {
        List<Ticket> tickets = createTickets(client_cart, paymentMethodId);
        List<TicketDTO> ticketDTOList = tickets.stream().map(ticketDTOMapper::apply).collect(Collectors.toList());
        String paymentUrl = null;
        PaymentMethod paymentMethod = paymentMethodRepository.getById(paymentMethodId);
        ticketRepository.saveAll(tickets);
        if (paymentMethod.getName().equals("VNPAY")) {
            paymentUrl = createVnPayPaymentUrl(tickets, ip);
        }
        return CheckoutResponse.builder()
                .tickets(ticketDTOList).paymentUrl(paymentUrl).build();
    }

    @Override
    public String handleVnPayResponse(Map<String, String> params) throws ParseException {
        String vnpTnxRef = params.get("vnp_TxnRef");
        String vnpTransactionNo = params.get("vnp_TransactionNo");
        String bankTransactionNo = params.get("vnp_BankTranNo");
        String vnpBankCode = params.get("vnp_BankCode");
        String vnpTransactionStatus = params.get("vnp_TransactionStatus");
        String vnp_PayDate = params.get("vnp_PayDate");
        String vnpCardType = params.get("vnp_CardType");
        if (vnpTransactionStatus.equals("00")) {
            OnlinePaymentResult paymentResult = paymentResultRepository.getByPaymentCode(vnpTnxRef);
            paymentResult.setBankCode(vnpBankCode);
            paymentResult.setTransactionNo(vnpTransactionNo);
            paymentResult.setBankTransactionNo(bankTransactionNo);
            paymentResult.setCardType(vnpCardType);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = formatter.parse(vnp_PayDate);
            Timestamp timestamp = new Timestamp(date.getTime());
            paymentResult.setConfirmAt(timestamp);
            for (Ticket ticket : paymentResult.getTickets()) {
                ticket.setPaidAt(timestamp);
            }
            paymentResultRepository.update(paymentResult);
        }
        return vnpTransactionStatus;
    }

    private String createVnPayPaymentUrl(List<Ticket> tickets,  String ip) throws UnsupportedEncodingException {
        Map<String, String > params = new HashMap<>();

        params.put("vnp_Command",environment.getProperty("vnpayCommand") );
        params.put("vnp_OrderType", environment.getProperty("orderType"));
        params.put("vnp_TmnCode", environment.getProperty("tmnCode"));
        params.put("vnp_CurrCode", environment.getProperty("vnpayCurrCode"));
        params.put("vnp_Locale",  environment.getProperty("vnpayLocale"));
        params.put("vnp_ReturnUrl", environment.getProperty("returnUrl"));
        params.put("vnp_OrderInfo","Pay for bus ticket");
        String vnpPaymentUrl = environment.getProperty("paymentUrl");
        String vnpHashSecureKey = environment.getProperty("hashSecureKey");
        long vnpTxnRef = new Date().getTime();
        long amount = 0;
        OnlinePaymentResult paymentResults = OnlinePaymentResult.builder()
                .paymentCode(String.valueOf(vnpTxnRef))
                .build();
        for (Ticket ticket : tickets) {
            amount += ticket.getSeatPrice();
            Cargo cargo = ticket.getCargo();
            if (cargo != null) {
                amount += cargo.getCargoPrice();
            }
            ticket.setPaymentResult(paymentResults);
        }
        ticketRepository.updateAll(tickets);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        calendar.add(Calendar.MINUTE, 15);
        String vnpExpiredData = formatter.format(calendar.getTime());
        params.put("vnp_CreateDate", vnpCreateDate);
        params.put("vnp_ExpireDate", vnpExpiredData);
        params.put("vnp_TxnRef", String.valueOf(vnpTxnRef));
        params.put("vnp_Amount", String.valueOf(amount * 100));
        params.put("vnp_IpAddr", ip);
        params.put("vnp_Version", environment.getProperty("vnpVersion"));
        List<String> fieldsName = new ArrayList<>(params.keySet());
        Collections.sort(fieldsName);
        Iterator iterator = fieldsName.iterator();
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        while (iterator.hasNext()) {
            String fieldName = iterator.next().toString();
            String fieldValue = params.get(fieldName);
            // Build hash data
            hashData.append(fieldName);
            hashData.append('=');
            hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
            //Build query
            query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
            query.append('=');
            query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
            if (iterator.hasNext()) {
                query.append('&');
                hashData.append('&');
            }
        }
        String queryUrl = query.toString();
        String vnpSecureHash = hmacSHA512(vnpHashSecureKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;


         return vnpPaymentUrl + "?" + queryUrl;
    }

    private String hmacSHA512(String key, String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public Map<Integer, StatisticsDTO> getAnnualRevenue(int year, Long companyId) {
        return ticketRepository.calculateAnnualRevenue(year, companyId);
    }

    @Override
    public Map<Integer, StatisticsDTO> getQuarterlyRevenue(int year, Long companyId) {
        return ticketRepository.calculateQuarterlyRevenue(year, companyId);
    }

    @Override
    public Map<Integer, StatisticsDTO> getDailyRevenue(int year, int month, int day, Long companyId) {
        return ticketRepository.calculateDailyRevenue(year, month, day, companyId);
    }

    @Override
    public List<TicketDTO> getTicketByUserId(Long userId) {
        List<Ticket> tickets = ticketRepository.findTicketsByUserId(userId);

        return tickets.stream().map(ticketDTOMapper::apply).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Ticket ticket = ticketRepository.getById(id);
        User  user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (hasObjectPermission(ticket, user)) {
            ticketRepository.delete(id);
        }
        else throw  new IllegalArgumentException("Access denied");
    }

    @Override
    public boolean hasObjectPermission(Ticket ticket, User user) {
        if (user.getRole().getName().equals("ADMIN")) {
            return true;
        }
        if (user.getId() == ticket.getCustomer().getId()) {
            return true;
        }
        return false;
    }

}
