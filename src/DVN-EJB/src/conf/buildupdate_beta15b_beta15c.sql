ALTER TABLE harvestingdataverse ADD COLUMN scheduleperiod varchar(255);
ALTER TABLE harvestingdataverse ALTER COLUMN scheduleperiod SET STORAGE EXTENDED;

ALTER TABLE harvestingdataverse ADD COLUMN schedulehourofday int4;;
ALTER TABLE harvestingdataverse ALTER COLUMN schedulehourofday SET STORAGE PLAIN;

ALTER TABLE harvestingdataverse ADD COLUMN scheduledayofweek int4;;
ALTER TABLE harvestingdataverse ALTER COLUMN scheduledayofweek SET STORAGE PLAIN;
