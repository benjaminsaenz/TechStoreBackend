package com.techstore.api.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminPingController {

    /**
     * Simple endpoint to verify that ADMIN auth works.
     */
    @GetMapping("/ping")
    public Map<String, Object> ping(Authentication auth) {
        Map<String, Object> res = new HashMap<>();
        res.put("ok", true);
        res.put("user", auth != null ? auth.getName() : null);
        res.put("authorities", auth != null ? auth.getAuthorities() : null);
        return res;
    }
}
