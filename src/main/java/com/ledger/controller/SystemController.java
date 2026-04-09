package com.ledger.controller;

import com.ledger.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SystemController {

    private final LedgerEntryRepository ledgerEntryRepository;

    @GetMapping("/system/invariant")
    public Map<String, Object> checkInvariant() {

        BigDecimal invariant =
                ledgerEntryRepository.getSystemBalanceInvariant();

        return Map.of(
                "ledgerInvariant", invariant
        );
    }
}