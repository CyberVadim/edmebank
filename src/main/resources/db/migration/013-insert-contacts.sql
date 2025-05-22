--liquibase formatted sql
--preconditions onFail:HALT onError:HALT
--changeset Koltsov_AE:013

INSERT INTO contacts (client_id, type, value, created_at, updated_at)
VALUES ('3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1', 'PHONE', '+79161234567', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;