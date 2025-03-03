package com.edmebank.clientmanagement.repository;

import com.edmebank.clientmanagement.model.Client;
import com.edmebank.clientmanagement.model.bank_product.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByClient(Client client);
}
