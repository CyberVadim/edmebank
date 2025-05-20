--liquibase formatted sql
--preconditions onFail:HALT onError:HALT
--changeset Elena_Yankovskaya:020

INSERT INTO credit_history (id, payment_date, scheduled_amount, paid_amount, delinquency_days, payment_method,
    created_at, updated_at)
    VALUES ('3f5c9f78-0c9a-4c1e-940d-17a2b7e232bb', '2023-11-15 00:00:00', 15000.00, 15000.00, 5, 'онлайн-перевод',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (id) DO NOTHING;
