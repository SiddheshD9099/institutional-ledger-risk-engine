package com.ledger.service;

import com.ledger.dto.TransferRequest;
import com.ledger.entity.*;
import com.ledger.repository.AccountRepository;
import com.ledger.repository.LedgerEntryRepository;
import com.ledger.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.ledger.repository.AccountLimitRepository;
import com.ledger.repository.AuditLogRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final AccountLimitRepository accountLimitRepository;
    private final AuditLogRepository auditLogRepository;
    private final MetricsService metricsService;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Transaction transfer(TransferRequest request) {

        Transaction transaction;

        try {

            transaction = transactionRepository.createTransaction(
                    UUID.randomUUID(),
                    request.getIdempotencyKey(),
                    "TRANSFER",
                    "PENDING"
            );

        } catch (Exception e) {

            Optional<Transaction> existing =
                    transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());

            if (existing.isPresent()) {
                return existing.get();
            }

            throw e;
        }

        // Lock accounts
        UUID firstId = request.getFromAccountId();
        UUID secondId = request.getToAccountId();

        if (firstId.compareTo(secondId) > 0) {
            UUID temp = firstId;
            firstId = secondId;
            secondId = temp;
        }

        Account firstAccount = accountRepository.lockAccountById(firstId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Account secondAccount = accountRepository.lockAccountById(secondId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        transaction.setStatus("PROCESSING");
        transactionRepository.save(transaction);

        // Balance validation
        BigDecimal balance =
                ledgerEntryRepository.getAccountBalance(request.getFromAccountId());

        if (balance.compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        AccountLimit limit =
                (AccountLimit) accountLimitRepository.findByAccountId(request.getFromAccountId())
                        .orElse(null);

        if (limit != null) {

            if (request.getAmount()
                    .compareTo(limit.getPerTxLimit()) > 0) {

                throw new RuntimeException("Per transaction limit exceeded");
            }

        }



        AuditLog log = new AuditLog();
        log.setId(UUID.randomUUID());
        log.setAction("TRANSFER_CREATED");
        log.setEntityType("TRANSACTION");
        log.setEntityId(transaction.getId());
        log.setCreatedAt(Instant.now());

        auditLogRepository.save(log);

        // Create DEBIT entry
        LedgerEntry debit = new LedgerEntry();
        debit.setId(UUID.randomUUID());
        debit.setTransactionId(transaction.getId());
        debit.setAccountId(request.getFromAccountId());
        debit.setEntryType("DEBIT");
        debit.setAmount(request.getAmount());
        debit.setCreatedAt(Instant.now());

        ledgerEntryRepository.save(debit);

        // Create CREDIT entry
        LedgerEntry credit = new LedgerEntry();
        credit.setId(UUID.randomUUID());
        credit.setTransactionId(transaction.getId());
        credit.setAccountId(request.getToAccountId());
        credit.setEntryType("CREDIT");
        credit.setAmount(request.getAmount());
        credit.setCreatedAt(Instant.now());

        ledgerEntryRepository.save(credit);

        transaction.setStatus("COMPLETED");
        transactionRepository.save(transaction);

        metricsService.incrementTransfers();

        return transaction;
    }
    public Transaction executeWithRetry(TransferRequest request) {

        int retries = 3;

        while (retries > 0) {
            try {
                return transfer(request);
            } catch (Exception e) {

                if (e.getMessage() != null &&
                        e.getMessage().contains("could not serialize")) {

                    retries--;

                    if (retries == 0) {
                        metricsService.incrementFailures();
                        throw new RuntimeException("Transaction retry limit exceeded");
                    }

                } else {
                    throw e;
                }
            }
        }

        throw new RuntimeException("Transaction failed");
    }
}