package com.softech.softech.controladores;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public/contact")
public class publicContactController {
    @Value("${softec.contact.address}") private String address;
    @Value("${softec.contact.phone}") private String phone;
    @Value("${softec.contact.whatsapp}") private String whatsapp;
    @Value("${softec.contact.hours}") private String hours;

    @GetMapping
    public Map<String, String> contact() {
        return Map.of("address", address, "phone", phone, "whatsapp", whatsapp, "hours", hours);
    }
}
