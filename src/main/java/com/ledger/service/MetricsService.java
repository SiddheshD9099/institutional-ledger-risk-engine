package com.ledger.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsService {

    private final AtomicLong transferCount = new AtomicLong();
    private final AtomicLong failedTransfers = new AtomicLong();

    public void incrementTransfers() {
        transferCount.incrementAndGet();
    }

    public void incrementFailures() {
        failedTransfers.incrementAndGet();
    }

    public long getTransferCount() {
        return transferCount.get();
    }

    public long getFailedTransfers() {
        return failedTransfers.get();
    }
}