package com.Savarona.controller;

import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class Controller {

    // Logger tanımla
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    // If 'hello' request came call that function
    @GetMapping("/hello")
    public String hello() {
        logger.info("📍 GET /hello endpoint çağrıldı");
        return "Hello World from Savarona Backend";
    }

    // Dinamik route - /hello/{name}
    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable String name) {
        logger.info("📍 GET /hello/{} endpoint çağrıldı - Gelen isim: {}", name, name);
        return "Hello " + name + "! Welcome to Savarona Backend!";
    }
}