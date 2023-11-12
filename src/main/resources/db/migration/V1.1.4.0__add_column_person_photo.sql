ALTER TABLE persons_person ADD COLUMN photo_uuid UUID DEFAULT NULL;

COMMENT ON COLUMN persons_person.photo_uuid IS 'Person photo uuid in file storage';