package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class TestController {

    @GetMapping("/api/hello")
    public String hello(HttpServletRequest request) {
        String uid = (String) request.getAttribute("uid");
        return "Hello user: " + uid;
    }

}
