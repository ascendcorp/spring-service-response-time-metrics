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

    @RequestMapping(path = "/demo1", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> demo1() {
        return serviceImpl.demo1();
    }

    @RequestMapping(path = "/demo2", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> demo2() {
        return serviceImpl.demo2();
    }
}
