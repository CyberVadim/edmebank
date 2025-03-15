package com.edmebank.clientmanagement.repository;

import com.edmebank.clientmanagement.model.ClientDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientDocumentRepository extends JpaRepository<ClientDocument, Long> {
}

