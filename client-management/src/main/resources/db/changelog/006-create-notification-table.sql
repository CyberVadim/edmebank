--liquibase formatted sql
--changeset Murad:006-create-notification-table.sql
--preconditions onFail:CONTINUE onError:CONTINUE

DROP TABLE IF EXISTS cm_schema.notifications;

CREATE TABLE cm_schema.notifications
(
    id                   BIGSERIAL PRIMARY KEY,                    -- Уникальный идентификатор уведомления
    client_id            UUID NOT NULL REFERENCES cm_schema.clients(id) ON DELETE CASCADE, -- Связь с клиентом
    email                VARCHAR(255),                             -- Email получателя
    message              TEXT NOT NULL,                            -- Текст уведомления
    type                 VARCHAR(50) NOT NULL,                     -- Тип уведомления (PASSPORT_EXPIRY, PRODUCT_UPDATE, DEPOSIT_INTEREST)
    status               VARCHAR(50) NOT NULL DEFAULT 'PENDING',   -- Статус уведомления (PENDING, SENT, DELIVERED, READ, FAILED)
    created_at           TIMESTAMP DEFAULT NOW() NOT NULL,         -- Дата создания уведомления
    last_attempt_at      TIMESTAMP,                                -- Дата последней попытки отправки
    attempt_count        INT DEFAULT 0,                            -- Количество попыток отправки
    client_confirmed     BOOLEAN DEFAULT FALSE,                    -- Подтверждение клиентом о прочтении
    client_response_date TIMESTAMP                                 -- Дата ответа клиента
);
--
-- -- Добавление тестовых данных
-- INSERT INTO cm_schema.notifications (client_id, email, message, type, status, created_at)
-- VALUES
--     ('10000000-0000-0000-0000-000000000201', 'darggun@gmail.com', 'Истечение срока паспорта', 'PASSPORT_EXPIRY',  'PENDING', NOW()),
--     ('10000000-0000-0000-0000-000000000202', 'murik311088@yandex.ru', 'Обновление условий по продукту', 'PRODUCT_UPDATE' , 'SENT', NOW());
