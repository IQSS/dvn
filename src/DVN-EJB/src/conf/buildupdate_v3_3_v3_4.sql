
ALTER TABLE vdcnetwork ADD COLUMN systememail character varying(255);

UPDATE vdcnetwork SET systememail = contactemail WHERE id = 1;

Alter TABLE vdcnetworkstats add downloadcount bigint;

Alter TABLE template add displayOnCreateDataverse boolean;

update template set displayOnCreateDataverse = true where vdc_id is null;
