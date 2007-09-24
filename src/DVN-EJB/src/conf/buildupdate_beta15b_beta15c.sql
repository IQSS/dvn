ALTER TABLE harvestingdataverse ADD COLUMN scheduleperiod varchar(255);
ALTER TABLE harvestingdataverse ALTER COLUMN scheduleperiod SET STORAGE EXTENDED;

ALTER TABLE harvestingdataverse ADD COLUMN schedulehourofday int4;;
ALTER TABLE harvestingdataverse ALTER COLUMN schedulehourofday SET STORAGE PLAIN;

ALTER TABLE harvestingdataverse ADD COLUMN scheduledayofweek int4;;
ALTER TABLE harvestingdataverse ALTER COLUMN scheduledayofweek SET STORAGE PLAIN;

DELETE FROM DataFileFormatType WHERE id=1;
DELETE FROM DataFileFormatType WHERE id=2;
DELETE FROM DataFileFormatType WHERE id=3;
INSERT INTO DataFileFormatType VALUES (1, 'D02', 'Splus');
INSERT INTO DataFileFormatType VALUES (2, 'D03', 'Stata');
INSERT INTO DataFileFormatType VALUES (3, 'D04', 'R');
