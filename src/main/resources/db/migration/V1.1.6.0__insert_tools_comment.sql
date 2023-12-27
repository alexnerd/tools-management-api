CREATE TABLE tools_comment (
    comment_id      BIGSERIAL   PRIMARY KEY,
    tool_id         BIGINT      NOT NULL,
    content         VARCHAR     NOT NULL,
    person_uuid     UUID        NOT NULL,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    FOREIGN KEY (tool_id)      REFERENCES tools_tool    (tool_id)
);

COMMENT ON TABLE  tools_comment               IS 'Module Tool - Tool comment';
COMMENT ON COLUMN tools_comment.comment_id    IS 'Primary key';
COMMENT ON COLUMN tools_comment.tool_id       IS 'Foreign key - id of tools_tool table';
COMMENT ON COLUMN tools_comment.content       IS 'Comment content';
COMMENT ON COLUMN tools_comment.person_uuid   IS 'Business key from Module Persons - Person person';
COMMENT ON COLUMN tools_comment.created_at    IS 'Creation record date';
COMMENT ON COLUMN tools_comment.updated_at    IS 'Update record date';