package ru.edmebank.clients.app.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edmebank.clients.domain.entity.Spouses;

import java.util.UUID;

public interface SpousesRepository extends JpaRepository<Spouses, UUID> {
}
