CREATE TABLE persons_label(
    label_id    BIGSERIAL PRIMARY KEY,
    name        VARCHAR   NOT NULL UNIQUE,
    is_archived BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX persons_label_name_gist_trgm_idx ON persons_label USING gist (name gist_trgm_ops);
CREATE INDEX persons_label_is_archived_idx ON persons_label (is_archived);


COMMENT ON TABLE persons_label              IS 'Module Persons - Person label';
COMMENT ON COLUMN persons_label.label_id    IS 'Primary key';
COMMENT ON COLUMN persons_label.name        IS 'Label name';
COMMENT ON COLUMN persons_label.is_archived IS 'Archived mark';
COMMENT ON COLUMN persons_label.created_at  IS 'Creation record date';
COMMENT ON COLUMN persons_label.updated_at  IS 'Update record date';