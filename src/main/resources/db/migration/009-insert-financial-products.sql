--liquibase formatted sql
--preconditions onFail:HALT onError:HALT
--changeset Koltsov_AE:009

INSERT INTO financial_products (id, product_type, name, min_amount, max_amount, interest_rate, term_months, is_active,
                                created_at, updated_at)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'CREDIT', 'Потребительский кредит', 100000.00, 5000000.00, 12.50, 24,
        true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);