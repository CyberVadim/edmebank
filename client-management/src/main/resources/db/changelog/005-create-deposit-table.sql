--liquibase formatted sql
--changeset Murad:005-create-deposit-table.sql
--preconditions onFail:CONTINUE onError:CONTINUE

CREATE TABLE IF NOT EXISTS cm_schema.deposits
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    balance    DECIMAL(15,2) NOT NULL,
    client_id  UUID REFERENCES cm_schema.clients(id) ON DELETE SET NULL
    );

-- Добавление тестовых данных
INSERT INTO cm_schema.deposits (id, balance, client_id)
VALUES
    ('40000000-0000-0000-0000-000000000201', 20000.00, '10000000-0000-0000-0000-000000000202');
