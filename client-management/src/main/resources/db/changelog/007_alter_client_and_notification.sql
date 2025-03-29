--liquibase formatted sql
--changeset Marchenko:007
--preconditions onFail:CONTINUE onError:CONTINUE

ALTER TABLE cm_schema.clients ADD COLUMN enable_notifications BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE cm_schema.notifications DROP COLUMN client_confirmed;