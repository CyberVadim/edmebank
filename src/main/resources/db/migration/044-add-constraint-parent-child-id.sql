--liquibase formatted sql
--preconditions onFail:HALT onError:HALT
--changeset vladimir_kapyrin:044
            ALTER TABLE client_data.account_hierarchy
            ADD CONSTRAINT ck_account_hierarchy_no_self_reference
            CHECK (parent_account_id IS DISTINCT FROM child_account_id);
