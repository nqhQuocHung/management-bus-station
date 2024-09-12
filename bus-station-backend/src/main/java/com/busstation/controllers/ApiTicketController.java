package com.busstation.controllers;

import com.busstation.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/ticket")
public class ApiTicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/cart/details")
    public ResponseEntity<Object> handleGetCartInfo(@RequestBody List<Map<String, String>> cart) {
        return ResponseEntity.ok(ticketService.getInfoFromCart(cart));
    }

    @PostMapping("/checkout/{id}")
    public ResponseEntity<Object> handleCheckout(@RequestBody List<Map<String, String>> cart, @PathVariable(name = "id") Long paymentMethodId, HttpServletRequest request) throws UnsupportedEncodingException {
        String ip = request.getRemoteAddr();
        return ResponseEntity.ok(ticketService.checkout(cart, paymentMethodId, ip));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
