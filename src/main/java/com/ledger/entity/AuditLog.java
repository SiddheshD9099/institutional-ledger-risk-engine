package com.ledger.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    private UUID id;

    private String action;

    private String entityType;

    private UUID entityId;

    private Instant createdAt;
}