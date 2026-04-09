package com.ledger.controller;

import com.ledger.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping("/metrics")
    public Map<String, Object> metrics() {

        return Map.of(
                "successfulTransfers",
                metricsService.getTransferCount(),

                "failedTransfers",
                metricsService.getFailedTransfers()
        );
    }
}