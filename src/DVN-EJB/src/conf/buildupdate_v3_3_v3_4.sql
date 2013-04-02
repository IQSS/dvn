
ALTER TABLE vdcnetwork ADD COLUMN systememail character varying(255);

UPDATE vdcnetwork SET systememail = contactemail WHERE id = 1;

Alter TABLE vdcnetworkstats add downloadcount bigint;

UPDATE vdcnetworkstats SET  downloadcount=0 WHERE downloadcount is null;

Alter TABLE template add displayOnCreateDataverse boolean;

update template set displayOnCreateDataverse = true where vdc_id is null;

UPDATE DataFileFormatType SET mimeType='text/plain' WHERE value='D02';
UPDATE DataFileFormatType SET mimeType='application/x-rlang-transport' WHERE value='D04';

ALTER TABLE studyfileactivity ADD CONSTRAINT unq_studyfileactivity_studyfile UNIQUE (studyfile_id);

insert into studyfileactivity (studyfile_id, study_id, downloadcount)
select sf.id, sf.study_id, 0
from studyfile sf left outer join studyfileactivity sfa on sf.id=sfa.studyfile_id
where sfa.id is null and sf.study_id is not null