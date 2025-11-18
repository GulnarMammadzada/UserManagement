package com.example.usermanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Slf4j
@Tag(name = "Health Check", description = "Health check endpoint for deployment verification")
public class HealthController {

    @GetMapping
    @Operation(summary = "Health check", description = "Returns application health status")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.debug("Health check requested");
        Map<String, Object> health = Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "service", "User Management Service",
                "version", "1.0.0"
        );
        return ResponseEntity.ok(health);
    }
}
