
--
-- first do DDL (this cannot be rollbacked)
--


ALTER TABLE studycomment ADD COLUMN studyversion_id bigint;
ALTER TABLE studycomment ALTER COLUMN studyversion_id SET STORAGE PLAIN;

ALTER TABLE studycomment
  ADD CONSTRAINT fk_studycomment_studyversion_id FOREIGN KEY (studyversion_id)
      REFERENCES studyversion (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


--
-- and now DML
--
begin;

insert into studyversion (versionnumber, versionnote, versionstate, study_id, metadata_id, version)
select 1, 'Initial version', rs.name, s.id, metadata_id, 1 from study s, reviewstate rs where s.reviewstate_id = rs.id;

insert into filemetadata (label, description, category, studyfile_id, studyversion_id, version)
select filename, sf.description, fc.name, sf.id,  sv.id, 1
from studyfile sf, filecategory fc, study s, studyversion sv
where sf.filecategory_id = fc.id
and sf.study_id = s.id
and s.id = sv.study_id;

update studycomment set studyversion_id = sv.id
from studyversion sv where studycomment.study_id = sv.study_id;

update studyversion set versionstate="DRAFT" where versionstate="New";

commit;


--
-- lastly, remove some unneeded colums and add some more constraints
--

ALTER TABLE studycomment DROP COLUMN study_id;
ALTER TABLE studycomment ALTER COLUMN studyversion_id SET NOT NULL;

ALTER TABLE studyfile DROP COLUMN filecategory_id;
alter table study drop column metadata_id;