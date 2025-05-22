package ru.edmebank.clients.app.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.edmebank.clients.domain.entity.CreditHistory;

import java.util.UUID;

public interface CreditHistoryRepository extends JpaRepository<CreditHistory, UUID> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE client_data.credit_history " +
            " SET delinquency_days = (CURRENT_DATE - payment_date::date) " +
            " WHERE payment_date < CURRENT_DATE " +
            " AND scheduled_amount > paid_amount;", nativeQuery = true)
    void updateDelinquencyDays();
}
