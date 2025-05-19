package ru.edmebank.clients.app.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edmebank.clients.domain.entity.ClientProducts;

import java.util.UUID;

public interface ClientProductsRepository extends JpaRepository<ClientProducts, UUID> {
}
