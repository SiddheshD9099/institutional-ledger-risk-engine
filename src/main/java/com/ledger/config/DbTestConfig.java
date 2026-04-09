package com.ledger.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbTestConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void testConnection() {
        String result = jdbcTemplate.queryForObject("SELECT NOW()", String.class);
        System.out.println("DB Connected at: " + result);
    }
}