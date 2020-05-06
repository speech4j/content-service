create schema if not exists metadata;

CREATE TABLE metadata.tenants
(
    id VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL,
    createddate TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description VARCHAR(255),
    modifieddate TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT "tenantsPK" PRIMARY KEY (id)
);

INSERT INTO metadata.tenants VALUES
('speech4j', true, '2020-6-04'::timestamp without time zone, 'Company', '2020-6-04'::timestamp without time zone);