CREATE TABLE persons_role(
    role_id    BIGSERIAL PRIMARY KEY,
    name        VARCHAR   NOT NULL UNIQUE,
    is_archived BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX persons_role_name_gist_trgm_idx ON persons_role USING gist (name gist_trgm_ops);
CREATE INDEX persons_role_is_archived_idx ON persons_role (is_archived);


COMMENT ON TABLE persons_role              IS 'Module Persons - Person role';
COMMENT ON COLUMN persons_role.role_id    IS 'Primary key';
COMMENT ON COLUMN persons_role.name        IS 'Role name';
COMMENT ON COLUMN persons_role.is_archived IS 'Archived mark';
COMMENT ON COLUMN persons_role.created_at  IS 'Creation record date';
COMMENT ON COLUMN persons_role.updated_at  IS 'Update record date';