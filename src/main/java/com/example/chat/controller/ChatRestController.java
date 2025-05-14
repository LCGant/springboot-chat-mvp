package com.example.chat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller simples para checar o status de saúde do serviço.
 */
@RestController
public class ChatRestController {

    /**
     * Endpoint de health check.
     *
     * @return "OK" se o serviço estiver em funcionamento
     */
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
