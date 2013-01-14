ALTER TABLE harvestingdataverse ADD COLUMN subsetrestricted bool;
ALTER TABLE harvestingdataverse ALTER COLUMN subsetrestricted SET STORAGE PLAIN;

update harvestingdataverse set subsetrestricted = filesrestricted;