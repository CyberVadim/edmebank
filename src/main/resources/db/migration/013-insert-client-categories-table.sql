--liquibase formatted sql
--preconditions onFail:HALT onError:HALT
--changeset Elena_Yankovskaya:013

INSERT INTO client_categories (id, category, loyalty_points, created_at, updated_at)
    VALUES ('3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1', 'VIP', 1500, '2023-11-01 00:00:00', CURRENT_TIMESTAMP)
    ON CONFLICT (id) DO NOTHING;