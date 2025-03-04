--liquibase formatted sql
--changeset Murad:001-create-client-table.sql
--preconditions onFail:CONTINUE onError:CONTINUE

CREATE TABLE IF NOT EXISTS cm_schema.clients
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),   -- Уникальный идентификатор клиента
    first_name          VARCHAR(100) NOT NULL,                        -- Имя
    last_name           VARCHAR(100) NOT NULL,                        -- Фамилия
    middle_name         VARCHAR(100),                                 -- Отчество
    date_of_birth       DATE NOT NULL,                                -- Дата рождения
    passport_number     VARCHAR(10) UNIQUE NOT NULL,                  -- Номер паспорта
    passport_series     VARCHAR(4),                                   -- Серия паспорта
    passport_issued_by  VARCHAR(255),                                 -- Кем выдан паспорт
    passport_issue_date DATE,                                         -- Дата выдачи паспорта
    address            VARCHAR(500),                                  -- Адрес клиента
    phone             VARCHAR(20),                                   -- Номер телефона
    email             VARCHAR(255) UNIQUE NOT NULL,                   -- Электронная почта
    inn               VARCHAR(12),                                    -- ИНН
    snils             VARCHAR(14),                                    -- СНИЛС
    aml_checked       BOOLEAN DEFAULT FALSE                           -- Статус AML/KYC проверки
    );

-- Добавление тестовых данных
INSERT INTO cm_schema.clients (id, first_name, last_name, date_of_birth, passport_number, email)
VALUES
    ('10000000-0000-0000-0000-000000000201', 'Иван', 'Иванов', '1985-06-15', '1234567890', 'darggun@gmail.com'),
    ('10000000-0000-0000-0000-000000000202', 'Мария', 'Петрова', '1990-08-22', '0987654321', 'maria.petrova@example.com');
