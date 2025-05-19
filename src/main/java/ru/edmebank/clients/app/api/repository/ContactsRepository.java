package ru.edmebank.clients.app.api.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.edmebank.clients.domain.entity.Contacts;

import java.util.UUID;

public interface ContactsRepository extends JpaRepository<Contacts, Contacts.ContactsId> {
}
