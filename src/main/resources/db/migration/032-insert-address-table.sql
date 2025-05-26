--liquibase formatted sql
--preconditions onFail:HALT onError:HALT
--changeset Timoshenko_AS:032

INSERT INTO client_data.addresses (
    client_id,
    type,
    postal_code,
    region,
    city,
    street,
    house,
    apartment
) VALUES (
             '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1',
             'REGISTRATION',
             '123456',
             'Московская область',
             'Москва',
             'Ленинский проспект',
             '10',
             '15'
         );