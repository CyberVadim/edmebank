--liquibase formatted sql
--preconditions onFail:HALT onError:HALT
--changeset Koltsov_AE:019

INSERT INTO spouses (id, client_id, spouse_client_id, full_name, marriage_date, marriage_contract_exists, social_status,
                     is_current, created_at, updated_at)
VALUES ('c2a4f275-f3c6-4c5d-af9a-3720e56b5b16', '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1', NULL, 'Maria Tkacheva',
        '2005-06-18', true, 'EMPLOYED', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;