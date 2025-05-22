--liquibase formatted sql
--preconditions onFail:HALT onError:HALT
--changeset Koltsov_AE:026

INSERT INTO financial_transactions (id, client_id, product_id, amount, currency, transaction_type, counterparty_info,
                                    created_at, updated_at)
VALUES ('a0a723ce-99e8-4dcb-b373-22e339f28a29', '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1000.50,
        'RUB', 'TRANSFER', '{"name": "John Doe", "account": "1234567890"}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;