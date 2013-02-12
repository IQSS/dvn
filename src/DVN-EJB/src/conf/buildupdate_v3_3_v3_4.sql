
ALTER TABLE vdcnetwork ADD COLUMN systememail character varying(255);

UPDATE vdcnetwork SET systememail = contactemail WHERE id = 1;

