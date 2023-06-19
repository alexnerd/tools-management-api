CREATE TABLE tools_category (
    category_id         BIGSERIAL   PRIMARY KEY,
    name                VARCHAR     NOT NULL UNIQUE,
    parent_category_id  BIGINT      DEFAULT NULL,
    is_archived         BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP   NOT NULL DEFAULT NOW(),
    FOREIGN KEY (parent_category_id) REFERENCES tools_category (category_id)
);

CREATE INDEX tools_category_name_gist_trgm_idx ON tools_category USING gist (name gist_trgm_ops);
CREATE INDEX tools_category_parent_category_id_idx ON tools_category (parent_category_id);
CREATE INDEX tools_category_is_archived_idx ON tools_category (is_archived);


COMMENT ON TABLE  tools_category                        IS 'Module Tool - Tool category';
COMMENT ON COLUMN tools_category.category_id            IS 'Primary key';
COMMENT ON COLUMN tools_category.name                   IS 'Category name';
COMMENT ON COLUMN tools_category.parent_category_id     IS 'Foreign key - Parent category id';
COMMENT ON COLUMN tools_category.is_archived            IS 'Archived mark';
COMMENT ON COLUMN tools_category.created_at             IS 'Creation record date';
COMMENT ON COLUMN tools_category.updated_at             IS 'Update record date';