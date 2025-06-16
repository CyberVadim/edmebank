package ru.edmebank.clients.app.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edmebank.clients.domain.entity.Account;

import java.util.UUID;

public interface AccountRepository extends JpaRepository <Account, UUID> {
}
