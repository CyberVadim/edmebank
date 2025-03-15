package com.edmebank.clientmanagement.model.bank_product;

import com.edmebank.clientmanagement.model.Client;

import java.util.UUID;

public interface BankProduct {
    UUID getId();
    Client getClient();
}

