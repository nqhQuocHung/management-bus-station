package com.busstation.controllers;

import com.busstation.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.text.ParseException;
import java.util.Map;

@Controller
@RequestMapping("/")
@PropertySource("classpath:configuration.properties")
public class UtilsController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private Environment environment;

    @GetMapping("/")
    public String index() {
        return "redirect:/admin";
    }

    @GetMapping("/payment-result/vnpay")
    public RedirectView vnPayReturnUrl(@RequestParam Map<String, String> params, RedirectAttributes attributes) throws ParseException {
        String code = ticketService.handleVnPayResponse(params);
        attributes.addAttribute("code", code);
        String redirectUrl = environment.getProperty("frontend.PaymentReturnUrl");
        return new RedirectView(redirectUrl);
    }


}
