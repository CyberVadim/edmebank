package ru.edmebank.clients.app.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edmebank.clients.domain.entity.Client;

import java.util.UUID;

public abstract class ClientRepository implements JpaRepository<Client, UUID> {
}
