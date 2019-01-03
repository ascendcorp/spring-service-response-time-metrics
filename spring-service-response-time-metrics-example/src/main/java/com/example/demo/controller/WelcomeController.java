package com.example.demo.controller;

import com.example.demo.service.ServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WelcomeController {

    private ServiceImpl serviceImpl;

    public WelcomeController(ServiceImpl serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @RequestMapping(path = "/welcome", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> welcome() {
        return serviceImpl.welcome();
    }
}
