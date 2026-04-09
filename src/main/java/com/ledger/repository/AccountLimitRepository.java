package com.ledger.repository;

import com.ledger.entity.AccountLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountLimitRepository
        extends JpaRepository<AccountLimit, UUID> {
    Optional<Object> findByAccountId(UUID fromAccountId);
}