--liquibase formatted sql
--changeset Marchenko:015
--preconditions onFail:HALT onError:HALT

INSERT INTO client_products (id, client_id, product_id, start_date, current_balance)
VALUES ('3f5c9f78-0c9a-4c1e-940d-17a2b7e232bb', '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1',
        'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '2025-01-01T00:00:00', '100000.00');