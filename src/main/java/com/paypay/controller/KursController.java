package com.paypay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paypay.dto.Response.Response;
import com.paypay.service.impl.KursImpl;

@RestController
@RequestMapping("/kurs-service")
public class KursController {
    Response response;
    
    @Autowired
    private KursImpl kursImpl;

    @PostMapping("/inquiry-kurs")
    public Response inquiryNpwp() throws Exception {
        response = kursImpl.inquiryKursBank();
        return response;
    }
}
