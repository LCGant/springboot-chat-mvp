package com.example.chat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatRestController {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
