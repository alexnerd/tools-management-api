CREATE TABLE persons_person_label (
    person_id   BIGINT        NOT NULL,
    label_id     BIGINT       NOT NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    PRIMARY KEY (person_id, label_id),
    FOREIGN KEY (person_id)     REFERENCES persons_person    (person_id),
    FOREIGN KEY (label_id)      REFERENCES persons_label      (label_id)
);

COMMENT ON TABLE  persons_person_label              IS 'Module Persons - table relation between person and label';
COMMENT ON COLUMN persons_person_label.person_id    IS 'Foreign key - id of persons_person table';
COMMENT ON COLUMN persons_person_label.label_id     IS 'Foreign key - id of persons_label table';
COMMENT ON COLUMN persons_person_label.created_at   IS 'Creation record date';