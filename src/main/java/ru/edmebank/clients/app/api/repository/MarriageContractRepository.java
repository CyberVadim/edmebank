package ru.edmebank.clients.app.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edmebank.clients.domain.entity.MarriageContract;

import java.util.UUID;

public interface MarriageContractRepository extends JpaRepository<MarriageContract, UUID> {
}
