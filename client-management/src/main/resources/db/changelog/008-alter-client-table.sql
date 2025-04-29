-- Migration to correct test data: move from Saint Petersburg to Moscow
--liquibase formatted sql
--changeset kadamovnk:008-alter-client-table.sql
--precondition onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:1
SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'cm_schema' AND table_name = 'clients';

UPDATE cm_schema.clients
SET
    address = 'г. Москва, ул. Вавилова, д. 39',
    passport_issued_by = 'ОВД Москвы'
WHERE id = '10000000-0000-0000-0000-000000000202';