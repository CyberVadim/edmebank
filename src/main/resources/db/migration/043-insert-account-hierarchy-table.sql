INSERT INTO account_hierarchy (
    parent_account_id,
    child_account_id
) VALUES
    -- Корневой счёт (текущий)
    (
        NULL,
        'd3ffbe44-5f3c-4e4a-b1a0-9ee9ef390d44'
    ),
    -- Дочерний счёт (сберегательный, родитель — текущий)
    (
        'd3ffbe44-5f3c-4e4a-b1a0-9ee9ef390d44',
        'e4ffcf55-6f4d-4f5b-c2b1-afe0ff401e55'
    );