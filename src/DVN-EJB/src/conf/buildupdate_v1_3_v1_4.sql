UPDATE pagedef SET path=regexp_replace(pagedef.path, '.jsp', '.xhtml', 'g');

-- Column: releasedate

-- ALTER TABLE vdc DROP COLUMN releasedate;

ALTER TABLE vdc ADD COLUMN releasedate timestamp;
ALTER TABLE vdc ALTER COLUMN releasedate SET STORAGE PLAIN;

-- Column: createddate

-- ALTER TABLE vdc DROP COLUMN createddate;

ALTER TABLE vdc ADD COLUMN createddate timestamp;
ALTER TABLE vdc ALTER COLUMN createddate SET STORAGE PLAIN;


-- Column: dvndescription

-- ALTER TABLE vdc DROP COLUMN dvndescription;

ALTER TABLE vdc ADD COLUMN dvndescription varchar(255);
ALTER TABLE vdc ALTER COLUMN dvndescription SET STORAGE EXTENDED;

ALTER TABLE harvestingdataverse RENAME COLUMN oaiserver TO serverurl;

drop table deletedstudy;

alter table oaiset
alter column definition type text;
