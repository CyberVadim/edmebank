--liquibase formatted sql
--preconditions onFail:HALT onError:HALT
--changeset Elena_Yankovskaya:037

INSERT INTO login_credentials (id, login, password, created_at, updated_at)
    VALUES ('3f5c9f78-0c9a-4c1e-940d-17a2b7e232b1', 'chichikov@sutulaya.su', '$2a$12$EmxCGTVLeLSp0qfFsgDK4.ItFI0bXGjQb/GaXA36.bHj6nChRU1vu',
            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (id) DO NOTHING;