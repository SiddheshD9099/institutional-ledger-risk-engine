package com.ledger.controller;

import com.ledger.dto.TransferRequest;
import com.ledger.entity.Transaction;
import com.ledger.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public Transaction transfer(@RequestBody TransferRequest request) {
        return transferService.executeWithRetry(request);
    }
}