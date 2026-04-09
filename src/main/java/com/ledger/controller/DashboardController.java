package com.ledger.controller;

import com.ledger.repository.LedgerEntryRepository;
import com.ledger.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final LedgerEntryRepository ledgerRepository;
    private final MetricsService metricsService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("transactions",
                ledgerRepository.getTransactionCount());

        model.addAttribute("ledgerEntries",
                ledgerRepository.getLedgerEntryCount());

        model.addAttribute("totalTransferred",
                ledgerRepository.getTotalTransferred());

        model.addAttribute("invariant",
                ledgerRepository.getSystemBalanceInvariant());

        model.addAttribute(
                "successfulTransfers",
                metricsService.getTransferCount()
        );

        model.addAttribute(
                "failedTransfers",
                metricsService.getFailedTransfers()
        );

        return "dashboard";
    }
}