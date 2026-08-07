-- Postgres has no ON UPDATE CURRENT_TIMESTAMP, and none is needed: every statement that
-- updates an existing auraskills_users row assigns last_updated = CURRENT_TIMESTAMP itself
-- (see SqlStorageProvider#saveUsersTable and #applyState), and nothing issues a bare UPDATE
-- against the table. If a plain UPDATE is ever added, this column needs a BEFORE UPDATE trigger.
ALTER TABLE auraskills_users
    ADD COLUMN last_updated TIMESTAMPTZ
        NOT NULL
        DEFAULT CURRENT_TIMESTAMP;
