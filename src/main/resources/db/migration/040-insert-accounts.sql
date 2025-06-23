-- Author: Vadim Protasov
-- Description: Вставка тестовых счетов различных типов

-- Текущий счёт - активный
INSERT INTO accounts (account_id, client_id, account_type, balance, currency, status, opened_at, created_at, updated_at)
VALUES ('f77bf1b4-4eee-440b-a6c4-0eff0da4629b', '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1', 'CHECKING', 5000.00, 'RUB', 'ACTIVE',
        '2025-01-15 10:00:00', '2025-01-15 10:00:00', '2025-06-20 14:30:00');

-- Депозитный счёт - активный
INSERT INTO accounts (account_id, client_id, account_type, balance, currency, status, opened_at, created_at, updated_at)
VALUES ('a3e8ddeb-9b0b-4c92-9aef-a3a3633fdca6', '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1', 'DEPOSIT', 100000.00, 'RUB', 'ACTIVE',
        '2025-02-20 11:00:00', '2025-02-20 11:00:00', '2025-06-20 14:30:00');

-- Кредитный счёт - активный
INSERT INTO accounts (account_id, client_id, account_type, balance, currency, status, opened_at, created_at, updated_at)
VALUES ('749b35a4-3427-4852-8cba-27517c503efd', '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1', 'LOAN_PRINCIPAL', 50000.00, 'RUB', 'ACTIVE',
        '2025-03-10 09:00:00', '2025-03-10 09:00:00', '2025-06-20 14:30:00');

-- Заблокированный счёт
INSERT INTO accounts (account_id, client_id, account_type, balance, currency, status, opened_at, created_at, updated_at)
VALUES ('70f9646f-1ef5-42f0-873a-db11a78505cf', '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1', 'CHECKING', 1000.00, 'RUB', 'BLOCKED',
        '2024-12-05 14:30:00', '2024-12-05 14:30:00', '2025-06-10 09:15:00');

-- Закрытый счёт
INSERT INTO accounts (account_id, client_id, account_type, balance, currency, status, opened_at, closed_at, created_at, updated_at)
VALUES ('db0d20f7-25f0-4f4a-a45a-08740c344f17', '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1', 'CHECKING', 0.00, 'RUB', 'CLOSED',
        '2024-10-20 10:00:00', '2025-05-15 16:45:00', '2024-10-20 10:00:00', '2025-05-15 16:45:00');

-- Счёт с отрицательным балансом
INSERT INTO accounts (account_id, client_id, account_type, balance, currency, status, opened_at, created_at, updated_at)
VALUES ('cf90421e-c59a-4c86-8e9c-5783b2989c81', '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1', 'CHECKING', -500.00, 'RUB', 'ACTIVE',
        '2025-01-10 13:20:00', '2025-01-10 13:20:00', '2025-06-18 11:30:00');

-- Счёт с истекшим сроком (открыт более 5 лет назад)
INSERT INTO accounts (account_id, client_id, account_type, balance, currency, status, opened_at, created_at, updated_at)
VALUES ('fae68062-8b5e-4bc2-9a2a-447e3d31e4bf', '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1', 'CHECKING', 3000.00, 'RUB', 'ACTIVE',
        '2020-01-15 10:00:00', '2020-01-15 10:00:00', '2025-06-15 09:45:00');

-- Замороженный счёт
INSERT INTO accounts (account_id, client_id, account_type, balance, currency, status, opened_at, created_at, updated_at)
VALUES ('0c028500-cfbb-47d1-a1f6-1f8b70c27200', '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1', 'CHECKING', 2000.00, 'RUB', 'FROZEN',
        '2024-11-05 15:40:00', '2024-11-05 15:40:00', '2025-06-05 10:20:00');