--liquibase formatted sql
--changeset Marchenko_GV:005
--preconditions onFail:HALT onError:HALT

INSERT INTO documents (id, client_id, type, storage_url)
VALUES ('3f5c9f78-0c9a-4c1e-940d-17a2b7e232bb', '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1', 'PASSPORT',
        'http://storage.example.com/123');