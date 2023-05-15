CREATE TABLE tools_brand (
    brand_id     BIGSERIAL   PRIMARY KEY,
    name         VARCHAR     NOT NULL UNIQUE,
    is_archived  BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX tools_brand_name_gist_trgm_idx ON tools_brand USING gist (name gist_trgm_ops);
CREATE INDEX tools_brand_is_archived_idx ON tools_brand (is_archived);


COMMENT ON TABLE  tools_brand               IS 'Module Tool - Tool brand';
COMMENT ON COLUMN tools_brand.brand_id      IS 'Primary key';
COMMENT ON COLUMN tools_brand.name          IS 'Brand name';
COMMENT ON COLUMN tools_brand.is_archived   IS 'Archived mark';
COMMENT ON COLUMN tools_brand.created_at    IS 'Creation record date';
COMMENT ON COLUMN tools_brand.updated_at    IS 'Update record date';