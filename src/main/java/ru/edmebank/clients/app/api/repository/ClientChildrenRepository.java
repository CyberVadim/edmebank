package ru.edmebank.clients.app.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edmebank.clients.domain.entity.ClientChildren;

import java.util.UUID;

public interface ClientChildrenRepository extends JpaRepository<ClientChildren, UUID> {
}
