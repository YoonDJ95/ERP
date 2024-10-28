package com.sap.sbb.erp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/status")
    public String status() {
        return "Application is running! 반갑당2233.";
    }

}
