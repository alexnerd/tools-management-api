CREATE TABLE tools_label (
    label_id     BIGSERIAL   PRIMARY KEY,
    name         VARCHAR     NOT NULL UNIQUE,
    is_archived  BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX tools_label_name_gist_trgm_idx ON tools_label USING gist (name gist_trgm_ops);
CREATE INDEX tools_label_is_archived_idx ON tools_label (is_archived);


COMMENT ON TABLE  tools_label               IS 'Module Tools - Tool label';
COMMENT ON COLUMN tools_label.label_id      IS 'Primary key';
COMMENT ON COLUMN tools_label.name          IS 'Label name';
COMMENT ON COLUMN tools_label.is_archived   IS 'Archived mark';
COMMENT ON COLUMN tools_label.created_at    IS 'Creation record date';
COMMENT ON COLUMN tools_label.updated_at    IS 'Update record date';