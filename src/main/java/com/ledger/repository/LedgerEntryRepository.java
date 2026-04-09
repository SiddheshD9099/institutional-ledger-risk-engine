package com.ledger.repository;

import com.ledger.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {

    @Query("SELECT COALESCE(SUM(le.amount),0) FROM LedgerEntry le WHERE le.accountId = :accountId")
    BigDecimal getAccountBalance(@Param("accountId") UUID accountId);

    @Query("SELECT COALESCE(SUM(le.amount),0) FROM LedgerEntry le")
    BigDecimal getSystemBalanceInvariant();

    @Query(value = "SELECT COUNT(*) FROM transactions", nativeQuery = true)
    long getTransactionCount();

    @Query(value = "SELECT COUNT(*) FROM ledger_entries", nativeQuery = true)
    long getLedgerEntryCount();

    @Query(value =
            """
            SELECT COALESCE(SUM(amount),0)
            FROM ledger_entries
            WHERE entry_type='DEBIT'
            """,
            nativeQuery = true)
    BigDecimal getTotalTransferred();
}