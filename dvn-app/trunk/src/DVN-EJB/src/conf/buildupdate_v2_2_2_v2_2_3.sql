ALTER TABLE vdc ADD COLUMN defaultSortOrder character varying(255);

ALTER TABLE harvestingdataverse ADD COLUMN lastsuccessfulnonzeroharvesttime timestamp without time zone;
ALTER TABLE harvestingdataverse ADD COLUMN failedstudycountnonzero bigint;
ALTER TABLE harvestingdataverse ADD COLUMN harvestedstudycountnonzero bigint;

ALTER TABLE vdcnetwork ADD COLUMN defaultDVSortColumn character varying(255);