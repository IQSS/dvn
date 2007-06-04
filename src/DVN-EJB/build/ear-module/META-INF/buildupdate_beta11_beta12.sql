--DB Changes for Beta_12 :


ALTER TABLE datavariable ALTER COLUMN questiontext type text;
ALTER TABLE datavariable ALTER COLUMN label type text; 

ALTER TABLE harvestingdataverse ADD COLUMN lastharvesttime timestamp;
ALTER TABLE harvestingdataverse ALTER COLUMN lastharvesttime SET STORAGE PLAIN;

ALTER TABLE vdcnetwork ADD COLUMN handleRegistration bool;
ALTER TABLE vdcnetwork ALTER COLUMN handleRegistration SET STORAGE PLAIN;
