package com.edmebank.clientmanagement.repository;

import com.edmebank.clientmanagement.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByPassportNumber(String passportNumber);

    Optional<Client> findByEmail(String email);
}

