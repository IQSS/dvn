
ALTER TABLE studyaccessrequest ADD COLUMN studyfile_id bigint;

ALTER TABLE studyaccessrequest
  ADD CONSTRAINT fk_studyaccessrequest_studyfile_id FOREIGN KEY (studyfile_id)
      REFERENCES studyfile (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- for each existing study access request, create a new entry for each restricted file
-- in the released study version and then delete all the old study access requests
insert into studyaccessrequest (study_id, vdcuser_id, studyfile_id)
select sar.study_id, sar.vdcuser_id, fmd.studyfile_id
from studyaccessrequest sar, filemetadata fmd, studyversion sv, studyfile sf
where sar.study_id = sv.study_id
and sv.id = fmd.studyversion_id
and fmd.studyfile_id = sf.id
and sf.restricted = true
and sv.versionstate='RELEASED';

delete from studyaccessrequest where studyfile_id is null;

