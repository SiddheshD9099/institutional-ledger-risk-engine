package com.ledger.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    private UUID id;

    @Column(name = "idempotency_key")
    private String idempotencyKey;

    private String type;

    private String status;

    @Column(name = "created_at")
    private Instant createdAt;
}