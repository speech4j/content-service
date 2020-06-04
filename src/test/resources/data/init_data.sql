create schema if not exists metadata;
create schema if not exists speech4j;

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
('speech4j', true, '2020-6-04'::timestamp without time zone, 'Company-1', '2020-6-04'::timestamp without time zone);

CREATE TABLE speech4j.content_content_tag
(
    content_guid VARCHAR(255) NOT NULL,
    tag_guid VARCHAR(255) NOT NULL
);
CREATE TABLE speech4j.content_contents (
    guid VARCHAR(255) NOT NULL,
    contenturl VARCHAR(255),
    tenantguid VARCHAR(255),
    transcript VARCHAR(255),
    CONSTRAINT "content_contentsPK" PRIMARY KEY (guid)
);
CREATE TABLE speech4j.content_tags (
    guid VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    CONSTRAINT "content_tagsPK" PRIMARY KEY (guid)
);

INSERT INTO speech4j.content_contents VALUES
('1', 'https://www.youtube.com/watch?v=zYoG7vTLpZk', 'speech4j',
 'Can not see the stars, but we are reaching, trying to get through the dark on a feeling.'),
('2', 'https://www.youtube.com/watch?v=i4Nn6Gx2Uv8', 'speech4j',
 'There was a time, I used to look into my eyes in a happy home, I was a king I had a golden throne.');
INSERT INTO speech4j.content_tags VALUES
('1', '#nightcore'), ('2', '#music'), ('3', '#relax'), ('4', '#mix');
INSERT INTO speech4j.content_content_tag VALUES
('1', '1'),('1', '2'),
('2', '3'), ('2', '4');
