--liquibase formatted sql
--changeset Murad:001-create-client-table.sql
--preconditions onFail:CONTINUE onError:CONTINUE

CREATE TABLE IF NOT EXISTS cm_schema.clients
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,  -- Уникальный идентификатор клиента
    first_name          VARCHAR(100) NOT NULL,                        -- Имя
    last_name           VARCHAR(100) NOT NULL,                        -- Фамилия
    middle_name         VARCHAR(100),                                 -- Отчество
    date_of_birth       DATE NOT NULL,                                -- Дата рождения
    passport_number     VARCHAR(10) UNIQUE NOT NULL,                  -- Серия паспорта и Номер паспорта
    passport_issued_by  VARCHAR(255),                                 -- Кем выдан паспорт
    passport_issue_date DATE,                                         -- Дата выдачи паспорта
    address            VARCHAR(500),                                  -- Адрес клиента
    phone             VARCHAR(20),                                    -- Номер телефона
    email             VARCHAR(255) UNIQUE NOT NULL,                   -- Электронная почта
    inn               VARCHAR(12),                                    -- ИНН
    snils             VARCHAR(14),                                    -- СНИЛС
    aml_checked       BOOLEAN NOT NULL DEFAULT FALSE,                 -- Статус AML/KYC проверки
    passport_expiry_date DATE                                         -- Дата истечения срока действия паспорта (если применимо)
    );

-- Добавление тестовых данных
INSERT INTO cm_schema.clients (id, first_name, last_name, middle_name, date_of_birth, passport_number, passport_issued_by, passport_issue_date, address, phone, email, inn, snils, aml_checked, passport_expiry_date)
VALUES
    ('10000000-0000-0000-0000-000000000201', 'Семен', 'Корнейчук', 'Дмитриевич', '1995-01-16', '4508421812', 'ОВД Москвы', '2018-05-30', 'г. Москва, ул. Ленина, д. 10', '+79633751002', 'edmebank@mail.ru', '056105627867', '112-233-445 95', TRUE, '2025-04-15'),
    ('10000000-0000-0000-0000-000000000202', 'Мария', 'Петрова', 'Викторовна', '1990-08-22', '4607098765', 'ОВД Санкт-Петербурга', '2006-09-12', 'г. Санкт-Петербург, пр. Невский, д. 20', '+79898924747', 'edme_bank_acceptor@edme.pro', '781234567890', '223-334-556 12', TRUE, '2025-05-22');