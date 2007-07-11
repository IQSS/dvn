--DB Changes for Beta_13 :


ALTER TABLE filecategory ALTER COLUMN name type text;

ALTER TABLE study ADD COLUMN harvestHoldings varchar(255);
ALTER TABLE study ALTER COLUMN harvestHoldings SET STORAGE EXTENDED;

