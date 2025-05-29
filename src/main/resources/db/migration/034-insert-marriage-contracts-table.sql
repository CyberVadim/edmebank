--liquibase formatted sql
--preconditions onFail:HALT onError:HALT
--changeset kadamovnk:034

INSERT INTO marriage_contracts (id, spouse_id, contract_number, signing_date, notary_info, scan_url, terms, validity_period, created_at, updated_at)
VALUES (
           'f2e8c6d9-a3b5-4d6e-9c8a-1b3d5e7f9a0c',
           'c2a4f275-f3c6-4c5d-af9a-3720e56b5b16',
           'MC-2023/05-42',
           '2023-05-15',
           'Нотариус г. Москвы Иванова А.С., лицензия №123456',
           'http://storage.example.com/contracts/mc-2023-05-42.pdf',
           '{"assetDivision": "separate", "propertyRights": {"premarital": "separate", "acquired": "joint"}}',
           '[2023-05-15, 2033-05-14]',
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       ) ON CONFLICT DO NOTHING;