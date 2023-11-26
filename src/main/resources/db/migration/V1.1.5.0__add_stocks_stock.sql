CREATE TABLE stocks_stock (
    stock_id                 BIGSERIAL  PRIMARY KEY,
    uuid                     UUID       NOT NULL UNIQUE,
    name                     VARCHAR    NOT NULL,
    address                  VARCHAR    NOT NULL,
    company_uuid             UUID       DEFAULT NULL,
    responsible_company_uuid UUID       DEFAULT NULL,
    responsible_person_uuid  UUID       DEFAULT NULL,
    is_archived              BOOLEAN    NOT NULL DEFAULT FALSE,
    created_at               TIMESTAMP  NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP  NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX stocks_stock_uuid_idx ON stocks_stock (uuid);
CREATE INDEX stocks_stock_name_gist_trgm_idx ON stocks_stock USING gist (name gist_trgm_ops);
CREATE INDEX stocks_stock_company_uuid_idx ON stocks_stock (company_uuid);
CREATE INDEX stocks_stock_responsible_company_uuid_idx ON stocks_stock (responsible_company_uuid);
CREATE INDEX stocks_stock_responsible_person_uuid_idx ON stocks_stock (responsible_person_uuid);
CREATE INDEX stocks_stock_is_archived_idx ON stocks_stock (is_archived);


COMMENT ON TABLE  stocks_stock                              IS 'Module Stocks - Stock stock';
COMMENT ON COLUMN stocks_stock.stock_id                     IS 'Primary key';
COMMENT ON COLUMN stocks_stock.uuid                         IS 'Business key';
COMMENT ON COLUMN stocks_stock.name                         IS 'Stock name';
COMMENT ON COLUMN stocks_stock.address                      IS 'Stock address';
COMMENT ON COLUMN stocks_stock.company_uuid                 IS 'Business key from Module Companies - Company company';
COMMENT ON COLUMN stocks_stock.responsible_company_uuid     IS 'Business key from Module Companies - Company company';
COMMENT ON COLUMN stocks_stock.responsible_person_uuid      IS 'Business key from Module Persons - Person person';
COMMENT ON COLUMN stocks_stock.is_archived                  IS 'Archived mark';
COMMENT ON COLUMN stocks_stock.created_at                   IS 'Creation record date';
COMMENT ON COLUMN stocks_stock.updated_at                   IS 'Update record date';