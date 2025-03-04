--liquibase formatted sql
--changeset Murad:006-create-notification-table.sql
--preconditions onFail:CONTINUE onError:CONTINUE

CREATE TABLE IF NOT EXISTS cm_schema.notifications
(
    id          BIGSERIAL PRIMARY KEY,                    -- Уникальный идентификатор уведомления
    client_id   UUID NOT NULL,                            -- ID клиента, которому отправляется уведомление
    email       VARCHAR(255),                             -- Email получателя (если требуется)
    message     TEXT NOT NULL,                            -- Текст уведомления
    type        VARCHAR(50) NOT NULL,                     -- Тип уведомления (INFO, WARNING, ERROR и т.д.)
    status      VARCHAR(50) NOT NULL DEFAULT 'PENDING',   -- Статус уведомления (PENDING, SENT, FAILED)
    timestamp   TIMESTAMP DEFAULT NOW()                   -- Время создания уведомления
    );

-- Добавление тестовых данных
INSERT INTO cm_schema.notifications (client_id, email, message, type, status)
VALUES
    ('10000000-0000-0000-0000-000000000201', 'ivan.ivanov@example.com', 'Добро пожаловать в EDME Bank!', 'INFO', 'SENT'),
    ('10000000-0000-0000-0000-000000000202', 'maria.petrova@example.com', 'Ваш кредитный лимит увеличен.', 'ALERT', 'PENDING');
