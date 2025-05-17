package ru.edmebank.clients.app.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edmebank.clients.domain.entity.Documents;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Documents, UUID> {
}
