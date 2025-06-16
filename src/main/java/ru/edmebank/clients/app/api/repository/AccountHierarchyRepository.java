package ru.edmebank.clients.app.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edmebank.clients.domain.entity.AccountHierarchy;

import java.util.UUID;

public interface AccountHierarchyRepository extends JpaRepository<AccountHierarchy, UUID> {
}
