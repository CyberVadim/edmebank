package com.edmebank.clientmanagement.repository;

import com.edmebank.clientmanagement.model.Client;
import com.edmebank.clientmanagement.model.bank_product.Credit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CreditRepository extends JpaRepository<Credit, UUID> {
    List<Credit> findByClient(Client client);
}
