package ru.edmebank.clients.app.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edmebank.clients.domain.entity.FinancialProducts;

import java.util.UUID;

public interface FinancialProductsRepository extends JpaRepository<FinancialProducts, UUID> {
}