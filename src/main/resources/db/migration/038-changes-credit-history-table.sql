-- liquibase formatted sql
-- changeset Yuriy_Yashkov:038
-- comment: Замена связи @MapsId на @ManyToOne с полем client_product_id

-- Удаляем старый внешний ключ
ALTER TABLE IF EXISTS cm_db.client_data.credit_history
    DROP CONSTRAINT IF EXISTS fk_credit_history_client_product;

-- Добавляем новое поле client_product_id с FK
ALTER TABLE cm_db.client_data.credit_history
    ADD COLUMN IF NOT EXISTS client_product_id UUID
        REFERENCES cm_db.client_data.client_products(id);

-- Копируем старый id в новое поле
UPDATE cm_db.client_data.credit_history
SET client_product_id = id;

-- Делаем поле обязательным
ALTER TABLE cm_db.client_data.credit_history
    ALTER COLUMN client_product_id SET NOT NULL;

-- Включаем расширение pgcrypto, если его нет
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Добавляем генерацию UUID по умолчанию
ALTER TABLE cm_db.client_data.credit_history
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

-- rollback
-- ALTER TABLE cm_db.client_data.credit_history DROP COLUMN IF EXISTS client_product_id;
-- ALTER TABLE cm_db.client_data.credit_history ALTER COLUMN id DROP DEFAULT;
-- ALTER TABLE cm_db.client_data.credit_history
--     ADD CONSTRAINT fk_credit_history_client_product
--     FOREIGN KEY (id) REFERENCES cm_db.client_data.client_products(id);

