package ru.edmebank.clients.app.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edmebank.clients.domain.entity.AccountProductLink;
import ru.edmebank.clients.domain.entity.AccountProductLinkId;

public interface AccountProductLinkRepository extends JpaRepository<AccountProductLink, AccountProductLinkId> {
}
