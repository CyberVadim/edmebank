--liquibase formatted sql
--changeset Murad:002-create-client-passports-table.sql
--preconditions onFail:CONTINUE onError:CONTINUE

CREATE TABLE cm_schema.client_passports (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    client_id UUID NOT NULL,
    document_path VARCHAR(512) NOT NULL,
    passport_number VARCHAR(20),
    passport_issue_date DATE,
    uploaded_at DATE DEFAULT CURRENT_DATE,
    CONSTRAINT fk_passport_client FOREIGN KEY (client_id) REFERENCES cm_schema.clients(id)
);
