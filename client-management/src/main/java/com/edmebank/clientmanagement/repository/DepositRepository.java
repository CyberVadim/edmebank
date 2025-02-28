package com.edmebank.clientmanagement.repository;

import com.edmebank.clientmanagement.model.bank_product.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DepositRepository extends JpaRepository<Deposit, UUID> {}
