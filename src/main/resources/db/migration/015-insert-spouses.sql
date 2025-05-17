--liquibase formatted sql
--preconditions onFail:HALT onError:HALT
--changeset Koltsov_AE:015

INSERT INTO spouses (id, client_id, spouse_client_id, full_name, marriage_date, marriage_contract_exists, social_status,
                     is_current)
VALUES ('c2a4f275-f3c6-4c5d-af9a-3720e56b5b16', '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1',
        '170422cf-3c25-4199-ae48-43d8df050815',
        NULL, '2005-06-18', true, 'WORKING', true)
ON CONFLICT DO NOTHING;