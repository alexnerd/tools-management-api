CREATE TABLE persons_person (
    person_id           BIGSERIAL       PRIMARY KEY,
    uuid                UUID            NOT NULL UNIQUE,
    phone_number        VARCHAR         DEFAULT NULL,
    company_uuid        UUID            DEFAULT NULL,
    surname             VARCHAR         NOT NULL,
    name                VARCHAR         NOT NULL,
    patronymic          VARCHAR         DEFAULT NULL,
    job_title           VARCHAR         NOT NULL,
    is_archived         BOOLEAN         NOT NULL DEFAULT FALSE,
    is_unregistered     BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX persons_person_uuid_idx ON persons_person (uuid);
CREATE INDEX persons_person_name_gist_trgm_idx ON persons_person USING gist (name gist_trgm_ops);
CREATE INDEX persons_person_company_uuid_idx ON persons_person (company_uuid);
CREATE INDEX persons_person_is_unregistered_idx ON persons_person (is_unregistered);


COMMENT ON TABLE  persons_person                    IS 'Module Persons - Person person';
COMMENT ON COLUMN persons_person.person_id          IS 'Primary key';
COMMENT ON COLUMN persons_person.uuid               IS 'Business key';
COMMENT ON COLUMN persons_person.phone_number       IS 'Person phone number';
COMMENT ON COLUMN persons_person.company_uuid       IS 'Business key from Module Companies - Company company';
COMMENT ON COLUMN persons_person.surname            IS 'Person surname';
COMMENT ON COLUMN persons_person.name               IS 'Person name';
COMMENT ON COLUMN persons_person.patronymic         IS 'Person patronymic';
COMMENT ON COLUMN persons_person.job_title          IS 'Person job title';
COMMENT ON COLUMN persons_person.is_archived        IS 'Person active flag';
COMMENT ON COLUMN persons_person.is_unregistered    IS 'Person unregistered flag';
COMMENT ON COLUMN persons_person.created_at         IS 'Creation record date';
COMMENT ON COLUMN persons_person.updated_at         IS 'Update record date';