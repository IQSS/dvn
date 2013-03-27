
ALTER TABLE vdcnetwork ADD COLUMN systememail character varying(255);

UPDATE vdcnetwork SET systememail = contactemail WHERE id = 1;

Alter TABLE vdcnetworkstats add downloadcount bigint;

UPDATE vdcnetworkstats SET  downloadcount=0 WHERE downloadcount is null;

Alter TABLE template add displayOnCreateDataverse boolean;

update template set displayOnCreateDataverse = true where vdc_id is null;

UPDATE DataFileFormatType SET mimeType='text/plain' WHERE value='D02';
UPDATE DataFileFormatType SET mimeType='application/x-rlang-transport' WHERE value='D04';
