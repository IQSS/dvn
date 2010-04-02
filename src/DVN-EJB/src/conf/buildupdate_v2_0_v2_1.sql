
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

update studyversion set versionstate='DRAFT' where versionstate='New';
update studyversion set versionstate='IN_REVIEW' where versionstate='In Review';
update studyversion set versionstate='RELEASED' where versionstate='Released';

commit;


--
-- lastly, remove some unneeded colums and add some more constraints
--
ALTER TABLE studycomment ALTER COLUMN studyversion_id SET NOT NULL;
ALTER TABLE studycomment DROP COLUMN study_id;
ALTER TABLE studycomment ALTER COLUMN studyversion_id SET NOT NULL;

ALTER TABLE studyfile DROP COLUMN filecategory_id;
alter table study drop column metadata_id;

--
-- End version related changees
--

--
-- New columns needed for contributor settings
--
ALTER TABLE vdc ADD COLUMN allowcontributorseditall boolean;
ALTER TABLE vdc ALTER COLUMN allowcontributorseditall SET STORAGE PLAIN;

ALTER TABLE vdc ADD COLUMN allowregistereduserstocontribute boolean;
ALTER TABLE vdc ALTER COLUMN allowregistereduserstocontribute SET STORAGE PLAIN;

update vdc set allowcontributorseditall = false;
update vdc set allowregistereduserstocontribute = false;

-- change data type for filesystemlocation
alter table studyfile alter column filesystemlocation type text;

--
-- Page authorization change for contributor settings
--
update pagedef set role_id = null where path = '/admin/OptionsPage.xhtml';
update pagedef set role_id = null where path = '/study/ManageStudiesPage.xhtml';
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
-- Modify variablecategory to use a double precision float for frequency:
--
ALTER TABLE variablecategory RENAME COLUMN frequency TO temp;
ALTER TABLE variablecategory ADD COLUMN frequency float;
UPDATE variablecategory SET frequency=temp;
ALTER TABLE variablecategory DROP COLUMN temp;
