package com.ledger.stress;

import com.ledger.dto.TransferRequest;
import com.ledger.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class TransferStressTest implements CommandLineRunner {

    private final TransferService transferService;

    @Override
    public void run(String... args) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(20);

        UUID accountA = UUID.fromString("6d310923-1619-4f2f-b5dc-71b62acfb413");
        UUID accountB = UUID.fromString("d51b2826-56c9-4c8a-99c6-264c8fad4cec");

        for (int i = 0; i < 100; i++) {

            int index = i;

            executor.submit(() -> {

                try {

                    TransferRequest req = new TransferRequest();

                    req.setFromAccountId(accountA);
                    req.setToAccountId(accountB);
                    req.setAmount(new BigDecimal("1"));
                    req.setIdempotencyKey("stress-" + index);

                    transferService.executeWithRetry(req);

                } catch (Exception e) {
                    System.out.println("Transfer failed: " + e.getMessage());
                }

            });

        }

        executor.shutdown();

    }
}