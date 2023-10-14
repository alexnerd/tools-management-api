CREATE TABLE persons_person_role (
    person_id   BIGINT       NOT NULL,
    role_id     BIGINT       NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    PRIMARY KEY (person_id, role_id),
    FOREIGN KEY (person_id)    REFERENCES persons_person    (person_id),
    FOREIGN KEY (role_id)      REFERENCES persons_role      (role_id)
);

COMMENT ON TABLE  persons_person_role              IS 'Module Persons - table relation between person and role';
COMMENT ON COLUMN persons_person_role.person_id    IS 'Foreign key - id of persons_person table';
COMMENT ON COLUMN persons_person_role.role_id      IS 'Foreign key - id of persons_role table';
COMMENT ON COLUMN persons_person_role.created_at   IS 'Creation record date';