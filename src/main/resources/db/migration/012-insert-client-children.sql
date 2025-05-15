--liquibase formatted sql
--changeset Lipskaya_AA:012
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM client_children WHERE id = '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1';

INSERT INTO client_children (
    id,
    client_id,
    full_name,
    birth_date,
    relation_type,
    is_dependent,
    created_at
) VALUES (
             '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1',
             '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1',
             'Иванов Петр Сидорович',
             '2010-05-15',
             'BIOLOGICAL',
             true,
             CURRENT_TIMESTAMP
         );