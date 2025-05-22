package ru.edmebank.clients.app.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edmebank.clients.domain.entity.FinancialTransactions;

import java.util.UUID;

public interface FinancialTransactionsRepository extends JpaRepository<FinancialTransactions, UUID> {
}
