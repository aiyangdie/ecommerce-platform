package com.aiyangdie.mall.controller;

import com.aiyangdie.mall.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Result<Map<String, String>> health() {
        return Result.ok(Map.of(
                "service", "mall-api",
                "status", "UP"
        ));
    }
}
