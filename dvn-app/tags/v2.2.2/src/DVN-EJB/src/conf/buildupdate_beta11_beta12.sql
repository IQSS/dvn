--DB Changes for Beta_12 :


ALTER TABLE datavariable ALTER COLUMN questiontext type text;
ALTER TABLE datavariable ALTER COLUMN label type text; 

ALTER TABLE vdcnetwork ADD COLUMN handleRegistration bool;
ALTER TABLE vdcnetwork ALTER COLUMN handleRegistration SET STORAGE PLAIN;
update vdcnetwork set handleregistration = false;

-- Column: name
ALTER TABLE vdcgroup ADD COLUMN name varchar(255);
ALTER TABLE vdcgroup ALTER COLUMN name SET STORAGE EXTENDED;

-- Column: description
ALTER TABLE vdcgroup ADD COLUMN description varchar(255);
ALTER TABLE vdcgroup ALTER COLUMN description SET STORAGE EXTENDED;

-- Column: lastharvesttime
ALTER TABLE harvestingdataverse ADD COLUMN lastharvesttime timestamp;
ALTER TABLE harvestingdataverse ALTER COLUMN lastharvesttime SET STORAGE PLAIN;

ALTER TABLE study ALTER COLUMN geographiccoverage type text;

UPDATE vdcnetwork set authority='TEST';

ALTER TABLE studytopicclass ALTER COLUMN value type text;

