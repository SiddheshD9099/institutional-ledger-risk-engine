package com.ledger.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "account_limits")
public class AccountLimit {

    @Id
    private UUID accountId;

    private BigDecimal perTxLimit;

    private BigDecimal dailyLimit;
}