--liquibase formatted sql
--preconditions onFail:HALT onError:HALT
--changeset vladimir_kapyrin:042
INSERT INTO account_product_links (
    account_id,
    client_product_id
) VALUES
    (
        'd3ffbe44-5f3c-4e4a-b1a0-9ee9ef390d44',
        '3f5c9f78-0c9a-4c1e-940d-17a2b7e232bb'
    )
    ON CONFLICT DO NOTHING;