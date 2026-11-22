package com.jobradar.controller;

import com.jobradar.service.JobCollectionService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
public class CollectionAdminController {
    private final JobCollectionService collectionService;
    private final String adminToken;

    public CollectionAdminController(JobCollectionService collectionService,
            @Value("${jobradar.admin.token:}") String adminToken) {
        this.collectionService = collectionService;
        this.adminToken = adminToken;
    }

    @PostMapping("/collect")
    public Map<String, Object> collect(
            @RequestHeader(value = "X-JobRadar-Admin-Token", required = false) String token,
            @RequestParam(required = false) String city) {
        if (adminToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Manual collection is disabled. Configure JOBRADAR_ADMIN_TOKEN.");
        }
        if (token == null || !MessageDigest.isEqual(adminToken.getBytes(StandardCharsets.UTF_8),
                token.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid admin token.");
        }
        if (city != null && (city.isBlank() || city.length() > 100)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "City must contain 1-100 characters.");
        }
        int processed = collectionService.collect(city);
        return Map.of("processed", processed,
                "configuredSources", collectionService.configuredSourceCount(),
                "city", city == null || city.isBlank() ? "configured default" : city.trim(),
                "message", "Collection run completed.");
    }
}