package com.ledger.repository;

import com.ledger.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query(
            value = """
    INSERT INTO transactions (id, idempotency_key, type, status, created_at)
    VALUES (:id, :key, :type, :status, NOW())
    RETURNING *
    """,
            nativeQuery = true
    )
    Transaction createTransaction(
            UUID id,
            String key,
            String type,
            String status
    );

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

}