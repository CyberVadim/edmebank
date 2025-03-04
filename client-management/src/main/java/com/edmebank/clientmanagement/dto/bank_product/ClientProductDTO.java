package com.edmebank.clientmanagement.dto.bank_product;

import com.edmebank.clientmanagement.model.bank_product.Account;
import com.edmebank.clientmanagement.model.bank_product.Credit;
import com.edmebank.clientmanagement.model.bank_product.Deposit;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.UUID;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClientProductDTO(
        UUID id,
        String productType,
        String accountNumber,
        BigDecimal amount,
        BigDecimal balance
) {
    public static ClientProductDTO fromAccount(Account account) {
        return new ClientProductDTO(account.getId(), "ACCOUNT", account.getAccountNumber(), null, null);
    }

    public static ClientProductDTO fromCredit(Credit credit) {
        return new ClientProductDTO(credit.getId(), "CREDIT", null, credit.getAmount(), null);
    }

    public static ClientProductDTO fromDeposit(Deposit deposit) {
        return new ClientProductDTO(deposit.getId(), "DEPOSIT", null, null, deposit.getBalance());
    }
}

