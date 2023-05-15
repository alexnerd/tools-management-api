CREATE TABLE tools_tool_label (
    tool_id     BIGINT       NOT NULL,
    label_id    BIGINT       NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    PRIMARY KEY (tool_id, label_id),
    FOREIGN KEY (tool_id)    REFERENCES tools_tool   (tool_id),
    FOREIGN KEY (label_id)   REFERENCES tools_label  (label_id)
);

COMMENT ON TABLE  tools_tool_label              IS 'Module Tool - table relation between tool and label';
COMMENT ON COLUMN tools_tool_label.tool_id      IS 'Foreign key - id of tools_tool table';
COMMENT ON COLUMN tools_tool_label.label_id     IS 'Foreign key - id of tools_label table';
COMMENT ON COLUMN tools_tool_label.created_at   IS 'Creation record date';