--liquibase formatted sql
--preconditions onFail:HALT onError:HALT
--changeset Timoshenko_AS:028

INSERT INTO client_data.passports (
    client_id,
    series,
    number,
    issue_date,
    issued_by,
    department_code
) VALUES (
             '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1',
             '4510',
             '682345',
             '2003-04-22',
             'ОТДЕЛЕНИЕМ УФМС РОССИИ ПО Г. МОСКВЕ ПО РАЙОНУ ТЁПЛЫЙ СТАН',
             '770-089'
         );