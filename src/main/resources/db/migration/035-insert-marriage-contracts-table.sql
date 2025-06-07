--liquibase formatted sql
--preconditions onFail:HALT onError:HALT
--changeset kadamovnk:035

INSERT INTO marriage_contracts (id, spouse_id, contract_number, signing_date, notary_info, scan_url, terms,
                                validity_period, created_at, updated_at)
VALUES ('3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1',
        '3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1',
        'MC-2023/05-42',
        '2023-05-15',
        'Нотариус г. Москвы Иванова А.С., лицензия №123456',
        'http://storage.example.com/contracts/mc-2023-05-42.pdf',
        '{"assetDivision": "separate", "propertyRights": {"premarital": "separate", "acquired": "joint"}}',
        '[2023-05-15, 2033-05-14]',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;