CREATE TABLE tools_tool (
    tool_id             BIGSERIAL       PRIMARY KEY,
    uuid                UUID            NOT NULL UNIQUE,
    name                VARCHAR         NOT NULL,
    is_consumable       BOOLEAN         NOT NULL DEFAULT FALSE,
    inventory_number    VARCHAR         DEFAULT NULL,
    responsible_uuid    UUID            DEFAULT NULL,
    project_uuid        UUID            DEFAULT NULL,
    price               NUMERIC(18,2)   DEFAULT NULL,
    ownership_type      VARCHAR(30)     NOT NULL,
    rent_till           TIMESTAMP       DEFAULT NULL,
    is_kit              BOOLEAN         NOT NULL DEFAULT FALSE,
    kit_uuid            UUID            DEFAULT NULL,
    brand_id            BIGINT          DEFAULT NULL,
    category_id         BIGINT          DEFAULT NULL,
    is_archived         BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    FOREIGN KEY (brand_id)      REFERENCES tools_brand    (brand_id),
    FOREIGN KEY (category_id)   REFERENCES tools_category (category_id)
);

CREATE UNIQUE INDEX tools_tool_uuid_idx ON tools_tool (uuid);
CREATE INDEX tools_tool_name_gist_trgm_idx ON tools_tool USING gist (name gist_trgm_ops);
CREATE INDEX tools_tool_responsible_uuid_idx ON tools_tool (responsible_uuid);
CREATE INDEX tools_tool_project_uuid_idx ON tools_tool (project_uuid);
CREATE INDEX tools_tool_kit_uuid_idx ON tools_tool (kit_uuid);
CREATE INDEX tools_tool_is_archived_idx ON tools_tool (is_archived);


COMMENT ON TABLE  tools_tool                    IS 'Module Tools - Tool tool';
COMMENT ON COLUMN tools_tool.tool_id            IS 'Primary key';
COMMENT ON COLUMN tools_tool.uuid               IS 'Business key';
COMMENT ON COLUMN tools_tool.name               IS 'Tool name';
COMMENT ON COLUMN tools_tool.is_consumable      IS 'Consumable mark';
COMMENT ON COLUMN tools_tool.inventory_number   IS 'Tool inventory number';
COMMENT ON COLUMN tools_tool.responsible_uuid   IS 'Business key from Module Persons - Person person';
COMMENT ON COLUMN tools_tool.project_uuid       IS 'Business key from Module Projects - Project project';
COMMENT ON COLUMN tools_tool.price              IS 'Tool price';
COMMENT ON COLUMN tools_tool.ownership_type     IS 'Type of ownership';
COMMENT ON COLUMN tools_tool.rent_till          IS 'last day of tool rent';
COMMENT ON COLUMN tools_tool.is_kit             IS 'Flag is the tool a kit';
COMMENT ON COLUMN tools_tool.kit_uuid           IS 'Kit uuid';
COMMENT ON COLUMN tools_tool.brand_id           IS 'Foreign key - id of tools_brand table';
COMMENT ON COLUMN tools_tool.category_id        IS 'Foreign key - id of tools_category table';
COMMENT ON COLUMN tools_tool.is_archived        IS 'Archived mark';
COMMENT ON COLUMN tools_tool.created_at         IS 'Creation record date';
COMMENT ON COLUMN tools_tool.updated_at         IS 'Update record date';