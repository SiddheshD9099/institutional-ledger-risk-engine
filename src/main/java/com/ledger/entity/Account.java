package com.ledger.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private UUID id;

    private String name;

    private String status;

}