package com.edmebank.clientmanagement.repository;

import com.edmebank.clientmanagement.model.Passport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PassportRepository extends JpaRepository<Passport, Long> {
    List<Passport> findAllByClientId(UUID clientId);
}


