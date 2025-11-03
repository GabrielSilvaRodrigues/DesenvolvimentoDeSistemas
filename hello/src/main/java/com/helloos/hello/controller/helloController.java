package com.helloos.hello.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class helloController {
    @GetMapping(value = "/hello")
    public String getHello(){
        return "Olá Mundo no Spring Boot";
    }
}
