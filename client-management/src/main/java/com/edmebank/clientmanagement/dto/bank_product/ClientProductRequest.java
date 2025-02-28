package com.edmebank.clientmanagement.dto.bank_product;

import lombok.Data;

import java.util.UUID;

@Data
public class ClientProductRequest {
    private UUID productId;
    private String productType; // ACCOUNT, CREDIT, DEPOSIT
}


