ALTER TABLE tools_tool ADD COLUMN photo_uuid UUID DEFAULT NULL;

COMMENT ON COLUMN tools_tool.photo_uuid IS 'Tool photo uuid in file storage';