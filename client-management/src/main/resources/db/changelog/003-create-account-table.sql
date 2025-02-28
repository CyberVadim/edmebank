--liquibase formatted sql
--changeset Murad:003-create-account-table.sql
--preconditions onFail:CONTINUE onError:CONTINUE

CREATE TABLE IF NOT EXISTS cm_schema.accounts
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_number  VARCHAR(50) UNIQUE NOT NULL,
    client_id       UUID REFERENCES cm_schema.clients(id) ON DELETE SET NULL
    );

-- Добавление тестовых данных
INSERT INTO cm_schema.accounts (id, account_number, client_id)
VALUES
    ('20000000-0000-0000-0000-000000000201', 'ACC123456789', '10000000-0000-0000-0000-000000000201'),
    ('20000000-0000-0000-0000-000000000202', 'ACC987654321', '10000000-0000-0000-0000-000000000202');