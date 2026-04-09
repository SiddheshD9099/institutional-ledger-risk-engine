# Institutional-Grade Ledger & Risk Engine

## Overview

This project simulates the backend infrastructure used in digital wallets, payment processors, and fintech platforms.
The system focuses on **financial correctness, transactional safety, and concurrency control** rather than simple CRUD functionality.

It implements a **double-entry ledger**, **idempotent transaction processing**, and a **risk validation engine** while maintaining strict **ACID guarantees** in PostgreSQL.

The goal of the project is to demonstrate how real financial systems maintain **data integrity under concurrent transactions**.

---

## Tech Stack

Backend

* Java
* Spring Boot
* Spring Data JPA

Database

* PostgreSQL

Other Components

* Thymeleaf (operational dashboard)
* Maven
* Spring Actuator

---

## System Architecture

The application follows a layered architecture:

Controller → Service → Repository → Database

```
Client Request
      ↓
TransferController
      ↓
TransferService
      ↓
Repositories
      ↓
PostgreSQL
```

Modules in the system:

* Accounts
* Transactions
* Ledger Entries
* Risk Engine
* Metrics System
* Monitoring Dashboard
* Audit Logging

---

## Core Financial Design Principles

### Double-Entry Accounting

Each transfer produces **two immutable ledger entries**:

Debit entry from sender
Credit entry to receiver

This guarantees the fundamental accounting invariant:

Total Credits − Total Debits = 0

---

### Immutable Ledger

Ledger entries cannot be modified or deleted.

Financial corrections must be performed using **reversal transactions**, ensuring complete auditability.

---

### Derived Account Balances

Balances are **not stored as mutable columns**.

Instead they are derived from ledger entries:

```
Balance = SUM(CREDITS) − SUM(DEBITS)
```

This eliminates race conditions and prevents inconsistent balances.

---

### Idempotent Transfers

Client retries should never cause duplicate financial operations.

Transfers use a **database-enforced idempotency key**:

```
UNIQUE(idempotency_key)
```

If the same request is retried, the system safely returns the existing transaction.

---

### Transactional Safety

Each transfer executes inside a database transaction with:

* SERIALIZABLE isolation level
* Row-level locks
* Deterministic lock ordering

This prevents anomalies such as **write skew and double spending**.

---

## Transfer Workflow

1. Begin database transaction
2. Lock involved accounts
3. Validate available balance
4. Validate risk limits
5. Insert transaction record
6. Insert debit ledger entry
7. Insert credit ledger entry
8. Commit transaction

If any step fails, the entire operation is rolled back.

---

## Risk Engine

The system enforces basic financial risk controls:

* Per-transaction transfer limit
* Daily transfer limit

Risk validation runs inside the same database transaction to prevent concurrency bypass.

---

## Monitoring & Observability

Operational visibility is provided through:

Metrics tracking
System invariant checks
Monitoring dashboard

Available endpoints:

```
/metrics
/dashboard
/system/invariant
/actuator/health
```

The invariant endpoint verifies that the ledger remains balanced.

---

## Stress Testing

A concurrency stress test simulates **100 parallel transfers** to validate:

* transaction safety
* retry logic for serialization failures
* ledger invariant preservation

---

## How to Run the Project

### 1. Clone the repository

```
git clone https://github.com/SiddheshD9099/institutional-ledger-risk-engine.git
cd institutional-ledger-risk-engine
```

### 2. Configure the database

Create a PostgreSQL database.

Copy the example configuration:

```
cp src/main/resources/application-dev.example.properties src/main/resources/application-dev.properties
```

Update credentials in the local file.

---

### 3. Run the application

```
mvn spring-boot:run
```

The application will start on:

```
http://localhost:8080
```

Dashboard:

```
http://localhost:8080/dashboard
```

---

## Production Configuration

The production profile uses environment variables instead of hardcoded credentials:

```
DB_URL
DB_USER
DB_PASSWORD
```

Example:

```
export DB_URL=jdbc:postgresql://localhost:5432/ledger_db
export DB_USER=postgres
export DB_PASSWORD=password
```

---

## Future Improvements

Possible enhancements to evolve the system toward large-scale fintech infrastructure:

* Event-driven ledger processing using Kafka
* Ledger snapshot tables for faster balance queries
* Redis idempotency cache
* Fraud detection rules
* Distributed tracing and observability
* Containerized deployment with Docker

---

## What This Project Demonstrates

* Transaction-safe backend design
* ACID compliance in financial systems
* Concurrency control in PostgreSQL
* Idempotent API design
* Ledger-based accounting architecture
* Production-style monitoring and observability

---

## Author

Siddhesh Desai
Computer Engineering Student | Backend Systems Enthusiast
