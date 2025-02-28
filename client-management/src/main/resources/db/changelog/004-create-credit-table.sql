--liquibase formatted sql
--changeset Murad:004-create-credit-table.sql
--preconditions onFail:CONTINUE onError:CONTINUE

CREATE TABLE IF NOT EXISTS cm_schema.credits
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    amount     DECIMAL(15,2) NOT NULL,
    client_id  UUID REFERENCES cm_schema.clients(id) ON DELETE SET NULL
    );

-- Добавление тестовых данных
INSERT INTO cm_schema.credits (id, amount, client_id)
VALUES
    ('30000000-0000-0000-0000-000000000201', 50000.00, '10000000-0000-0000-0000-000000000201');
