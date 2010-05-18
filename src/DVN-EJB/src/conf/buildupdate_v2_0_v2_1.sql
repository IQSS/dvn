
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
-- New columns needed for contributor settings
--
ALTER TABLE vdc ADD COLUMN allowcontributorseditall boolean;
ALTER TABLE vdc ALTER COLUMN allowcontributorseditall SET STORAGE PLAIN;

ALTER TABLE vdc ADD COLUMN allowregistereduserstocontribute boolean;
ALTER TABLE vdc ALTER COLUMN allowregistereduserstocontribute SET STORAGE PLAIN;

-- change data type for filesystemlocation
alter table studyfile alter column filesystemlocation type text;

--
-- Modify variablecategory to use a double precision float for frequency:
--
ALTER TABLE variablecategory RENAME COLUMN frequency TO temp;
ALTER TABLE variablecategory ADD COLUMN frequency float;
UPDATE variablecategory SET frequency=temp;
ALTER TABLE variablecategory DROP COLUMN temp;

--
-- Add null constraints to existing columns
--
ALTER TABLE datatable ALTER COLUMN studyfile_id SET NOT NULL;
ALTER TABLE datavariable ALTER COLUMN datatable_id SET NOT NULL;
ALTER TABLE datavariable ALTER COLUMN variableformattype_id SET NOT NULL;
ALTER TABLE harvestingdataverse ALTER COLUMN harvestformattype_id SET NOT NULL;
ALTER TABLE loginaffiliate ALTER COLUMN usergroup_id SET NOT NULL;
ALTER TABLE logindomain ALTER COLUMN usergroup_id SET NOT NULL;
ALTER TABLE networkrolerequest ALTER COLUMN vdcuser_id SET NOT NULL;
ALTER TABLE networkrolerequest ALTER COLUMN networkrole_id SET NOT NULL;
ALTER TABLE rolerequest ALTER COLUMN vdcuser_id SET NOT NULL;
ALTER TABLE rolerequest ALTER COLUMN vdc_id SET NOT NULL;
ALTER TABLE rolerequest ALTER COLUMN role_id SET NOT NULL;
ALTER TABLE studyaccessrequest ALTER COLUMN vdcuser_id SET NOT NULL;
ALTER TABLE studyaccessrequest ALTER COLUMN study_id SET NOT NULL;
ALTER TABLE studyrequest ALTER COLUMN vdcuser_id SET NOT NULL;
ALTER TABLE studyrequest ALTER COLUMN study_id SET NOT NULL;
ALTER TABLE studycomment ALTER COLUMN commentcreator_id SET NOT NULL;
ALTER TABLE studydistributor ALTER COLUMN metadata_id SET NOT NULL;
ALTER TABLE studyfileactivity ALTER COLUMN studyfile_id SET NOT NULL;
ALTER TABLE studyfileactivity ALTER COLUMN study_id SET NOT NULL;
ALTER TABLE studylock ALTER COLUMN user_id SET NOT NULL;
ALTER TABLE studylock ALTER COLUMN study_id SET NOT NULL;
ALTER TABLE summarystatistic ALTER COLUMN type_id SET NOT NULL;
ALTER TABLE summarystatistic ALTER COLUMN datavariable_id SET NOT NULL;
ALTER TABLE study ALTER COLUMN creator_id SET NOT NULL;
ALTER TABLE study ALTER COLUMN template_id SET NOT NULL;
ALTER TABLE study ALTER COLUMN owner_id SET NOT NULL;
ALTER TABLE vdc ALTER COLUMN creator_id SET NOT NULL;
ALTER TABLE vdc ALTER COLUMN defaulttemplate_id SET NOT NULL;
ALTER TABLE templatefield ALTER COLUMN fieldinputlevel_id SET NOT NULL;
ALTER TABLE templatefield ALTER COLUMN template_id SET NOT NULL;
ALTER TABLE templatefield ALTER COLUMN studyfield_id SET NOT NULL;
ALTER TABLE templatefieldselectvalue ALTER COLUMN templatefield_id SET NOT NULL;
ALTER TABLE termsofuse ALTER COLUMN vdc_id SET NOT NULL;
ALTER TABLE variablecategory ALTER COLUMN datavariable_id SET NOT NULL;
ALTER TABLE variablerange ALTER COLUMN datavariable_id SET NOT NULL;
ALTER TABLE variablerangeitem ALTER COLUMN datavariable_id SET NOT NULL;
ALTER TABLE vdcgrouprelationship ALTER COLUMN subgroup_id SET NOT NULL;
ALTER TABLE vdcgrouprelationship ALTER COLUMN group_id SET NOT NULL;
ALTER TABLE vdcrole ALTER COLUMN vdcuser_id SET NOT NULL;
ALTER TABLE vdcrole ALTER COLUMN vdc_id SET NOT NULL;
ALTER TABLE vdcrole ALTER COLUMN role_id SET NOT NULL;

--
-- and now DML
--
begin;

-- for release time, we use the lastupdatetime
insert into studyversion (versionnumber, versionnote, versionstate,  createtime, lastupdatetime, study_id, metadata_id, version)
select 1, 'Initial version', rs.name, s.createtime, s.lastupdatetime, s.id, metadata_id, 1 from study s, reviewstate rs where s.reviewstate_id = rs.id;

-- first add last updaters as contributors
insert into versioncontributor (studyversion_id, contributor_id, lastupdatetime, version)
select sv.id, s.lastupdater_id, s.lastupdatetime, 1 from studyversion sv, study s where sv.study_id = s.id;

-- if creator is a different user, then add creator as a contributor (suing createtime for lastupdatetime as best guess)
insert into versioncontributor (studyversion_id, contributor_id, lastupdatetime, version)
select sv.id, s.creator_id, s.createtime, 1 from studyversion sv, study s where sv.study_id = s.id and s.creator_id != s.lastupdater_id;

insert into filemetadata (label, description, category, studyfile_id, studyversion_id, version)
select filename, sf.description, fc.name, sf.id,  sv.id, 1
from studyfile sf, filecategory fc, study s, studyversion sv
where sf.filecategory_id = fc.id
and sf.study_id = s.id
and s.id = sv.study_id;

update studycomment set studyversion_id = sv.id
from studyversion sv where studycomment.study_id = sv.study_id;

update studyversion set versionstate='DRAFT' where versionstate='New';
update studyversion set versionstate='IN_REVIEW' where versionstate='In Review';
update studyversion set versionstate='RELEASED' where versionstate='Released';

update studyversion set releasetime = lastupdatetime where versionstate='RELEASED';


-- set contributor setting booleans
update vdc set allowcontributorseditall = false;
update vdc set allowregistereduserstocontribute = false;
update vdc set allowregistereduserstocontribute = true where allowcontributorrequests = true;

--
-- Page authorization change for contributor settings
--
update pagedef set role_id = null where path = '/admin/OptionsPage.xhtml';
update pagedef set role_id = null where path = '/study/ManageStudiesPage.xhtml';

---
--- Other page def changes
---
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'DeaccessionStudyPage', '/study/DeaccessionStudyPage.xhtml', 2, null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'AccountOptionsPage', '/login/AccountOptionsPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'AccountPage', '/login/AccountPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'StudyVersionDifferencesPage', '/study/StudyVersionDifferencesPage.xhtml', null,null );

--
-- Fix to template field recommendation levels
--
update templatefield set fieldinputlevel_id = 3 where studyfield_id in ( 28, 31);
commit;


--
-- lastly, remove some unneeded colums and add some more constraints
--

ALTER TABLE study DROP COLUMN reviewstate_id;
ALTER TABLE vdc DROP COLUMN reviewstate_id;

drop table reviewstate;

ALTER TABLE studycomment ALTER COLUMN studyversion_id SET NOT NULL;
ALTER TABLE studycomment DROP COLUMN study_id;
ALTER TABLE studycomment ALTER COLUMN studyversion_id SET NOT NULL;

ALTER TABLE studyfile DROP COLUMN filecategory_id;
alter table study drop column metadata_id;




