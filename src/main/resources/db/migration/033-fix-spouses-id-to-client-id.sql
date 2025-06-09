--liquibase formatted sql
--preconditions onFail:HALT onError:HALT
--changeset kadamovnk:33

UPDATE spouses
SET id = client_id
WHERE id <> client_id;