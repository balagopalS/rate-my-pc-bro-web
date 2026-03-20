package com.ratemypcbro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/ping")
public class PingController {

    @GetMapping
    public Map<String, Object> ping() {
        return Map.of(
            "status", "UP",
            "message", "Rate My PC Bro API is running",
            "timestamp", LocalDateTime.now()
        );
    }
}
