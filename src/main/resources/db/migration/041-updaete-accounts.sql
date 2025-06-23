-- Author: Vadim Protasov
-- Description: Обновление существующих тестовых счетов

-- Обновление баланса для текущего счёта
UPDATE accounts
SET balance = 6500.00,
    updated_at = '2025-06-23 11:00:00'
WHERE account_id = 'f77bf1b4-4eee-440b-a6c4-0eff0da4629b';

-- Обновление статуса для заблокированного счёта (разблокировка)
UPDATE accounts
SET status = 'ACTIVE',
    updated_at = '2025-06-23 11:05:00'
WHERE account_id = '70f9646f-1ef5-42f0-873a-db11a78505cf';

-- Обновление баланса для счёта с отрицательным балансом (погашение задолженности)
UPDATE accounts
SET balance = 0.00,
    updated_at = '2025-06-23 11:08:00'
WHERE account_id = 'cf90421e-c59a-4c86-8e9c-5783b2989c81';