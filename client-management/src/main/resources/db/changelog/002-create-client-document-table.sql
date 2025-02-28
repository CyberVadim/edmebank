--liquibase formatted sql
--changeset Murad:002-create-client-document-table.sql
--preconditions onFail:CONTINUE onError:CONTINUE

CREATE TABLE IF NOT EXISTS cm_schema.client_documents
(
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,  -- Уникальный идентификатор документа
    client_id       UUID NOT NULL,                                      -- Идентификатор клиента
    document_path   VARCHAR(500) NOT NULL,                              -- Путь к документу
    CONSTRAINT fk_client_id FOREIGN KEY (client_id) REFERENCES cm_schema.clients (id) ON DELETE CASCADE
    );

-- Добавление тестовых данных
INSERT INTO cm_schema.client_documents (client_id, document_path)
VALUES
    ('10000000-0000-0000-0000-000000000201', 'D:\EDMEData\documents\client1_passport.jpg'),
    ('10000000-0000-0000-0000-000000000202', 'D:\EDMEData\documents\client2_passport.jpg');
