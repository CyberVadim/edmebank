package ru.edmebank.clients.app.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edmebank.clients.domain.entity.CreditHistory;

import java.util.UUID;

public interface CreditHistoryRepository extends JpaRepository<CreditHistory, UUID> {
}
