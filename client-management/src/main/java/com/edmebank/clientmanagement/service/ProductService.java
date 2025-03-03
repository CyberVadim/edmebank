package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.dto.bank_product.ClientProductDTO;
import com.edmebank.clientmanagement.dto.bank_product.ClientProductRequest;
import com.edmebank.clientmanagement.exception.ClientNotFoundException;
import com.edmebank.clientmanagement.model.Client;
import com.edmebank.clientmanagement.model.bank_product.Account;
import com.edmebank.clientmanagement.model.bank_product.BankProduct;
import com.edmebank.clientmanagement.model.bank_product.Credit;
import com.edmebank.clientmanagement.model.bank_product.Deposit;
import com.edmebank.clientmanagement.repository.AccountRepository;
import com.edmebank.clientmanagement.repository.ClientRepository;
import com.edmebank.clientmanagement.repository.CreditRepository;
import com.edmebank.clientmanagement.repository.DepositRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final CreditRepository creditRepository;
    private final DepositRepository depositRepository;


    @Transactional
    public void linkProductToClient(UUID clientId, ClientProductRequest request) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Клиент не найден"));

        switch (request.getProductType()) {
            case "ACCOUNT":
                Account account = new Account();
                account.setAccountNumber(generateAccountNumber());
                account.setClient(client);
                accountRepository.save(account);
                break;
            case "CREDIT":
                Credit credit = new Credit();
                credit.setAmount(BigDecimal.valueOf(0.0));
                credit.setClient(client);
                creditRepository.save(credit);
                break;
            case "DEPOSIT":
                Deposit deposit = new Deposit();
                deposit.setBalance(BigDecimal.valueOf(0.0));
                deposit.setClient(client);
                depositRepository.save(deposit);
                break;
            default:
                throw new IllegalArgumentException("Некорректный тип продукта");
        }
    }

    private String generateAccountNumber() {
        return "ACC" + (1000000000 + new Random().nextInt(900000000));
    }

    public List<String> getAvailableProducts(UUID clientId) {
        return List.of("ACCOUNT", "CREDIT", "DEPOSIT");
    }

    public List<ClientProductDTO> getClientProducts(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Клиент не найден"));

        List<ClientProductDTO> accounts = accountRepository.findByClient(client)
                .stream()
                .map(ClientProductDTO::fromAccount)
                .collect(Collectors.toList());

        List<ClientProductDTO> credits = creditRepository.findByClient(client)
                .stream()
                .map(ClientProductDTO::fromCredit)
                .collect(Collectors.toList());

        List<ClientProductDTO> deposits = depositRepository.findByClient(client)
                .stream()
                .map(ClientProductDTO::fromDeposit)
                .collect(Collectors.toList());

        return Stream.concat(Stream.concat(accounts.stream(), credits.stream()), deposits.stream())
                .collect(Collectors.toList());
    }

}
