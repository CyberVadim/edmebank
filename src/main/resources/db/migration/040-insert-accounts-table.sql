--liquibase formatted sql
 --preconditions onFail:HALT onError:HALT
 --changeset vladimir_kapyrin:040
INSERT INTO accounts (
    account_id,
    client_id,
    account_type,
    balance,
    currency,
    status,
    opened_at,
    closed_at
) VALUES
    -- Счёт 1: текущий, открыт
    (
        'd3ffbe44-5f3c-4e4a-b1a0-9ee9ef390d44',
        '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1',
        'текущий',
        25350.55,
        'RUB',
        'открыт',
        '2023-11-01 09:00:00',
        NULL
    ),
    -- Счёт 2: сберегательный, открыт
    (
        'e4ffcf55-6f4d-4f5b-c2b1-afe0ff401e55',
        '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1',
        'сберегательный',
        1000000.00,
        'RUB',
        'открыт',
        '2023-11-01 09:00:00',
        NULL
    ),
    -- Счёт 3: текущий, закрыт
    (
        'f5ffdf66-7f5e-4f6c-d3c2-bfe1ff512f66',
        '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1',
        'текущий',
        0.00,
        'RUB',
        'закрыт',
        '2023-11-01 09:00:00',
        '2024-06-01 12:00:00'
    )
    ON CONFLICT DO NOTHING;