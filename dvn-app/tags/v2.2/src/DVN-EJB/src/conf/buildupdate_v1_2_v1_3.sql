
-- Add metadata_id to study table and copy primary key into it
begin;

update dvnversion set versionnumber=1, buildnumber=3;

ALTER TABLE study ADD COLUMN metadata_id int8;
ALTER TABLE study ALTER COLUMN metadata_id SET STORAGE PLAIN;
update study set metadata_id = id;


CREATE TABLE metadata
(
  id serial NOT NULL,
  citationrequirements text,
  depositorrequirements text,
  conditions text,
  unf text,
  disclaimer text,
  title text,
  productiondate text,
  productionplace text,
  fundingagency text,
  confidentialitydeclaration text,
  seriesinformation text,
  distributiondate text,
  timeperiodcoveredstart text,
  distributorcontact text,
  dateofcollectionstart text,
  distributorcontactaffiliation text,
  country text,
  distributorcontactemail text,
  geographicunit text,
  depositor text,
  universe text,
  dateofdeposit text,
  timemethod text,
  frequencyofdatacollection text,
  deviationsfromsampledesign text,
  versiondate text,
  researchinstrument text,
  replicationfor text,
  originofsources text,
  subtitle text,
  accesstosources text,
  actionstominimizeloss text,
  weighting text,
  harvestholdings varchar(255),
  studylevelerrornotes text,
  harvestidentifier varchar(255),
  samplingerrorestimate text,
  harvestdvtermsofuse text,
  placeofaccess text,
  harvestdvntermsofuse text,
  availabilitystatus text,
  geographiccoverage text,
  studycompletion text,
  unitofanalysis text,
  specialpermissions text,
  kindofdata text,
  contact text,
  datacollector text,
  samplingprocedure text,
  collectionmode text,
  seriesname text,
  datasources text,
  timeperiodcoveredend text,
  characteristicofsources text,
  datacollectionsituation text,
  controloperations text,
  version int8,
  cleaningoperations text,
  dateofcollectionend text,
  responserate text,
  otherdataappraisal text,
  restrictions text,
  originalarchive text,
  studyversion text,
  collectionsize text

)
WITHOUT OIDS;
ALTER TABLE metadata OWNER TO "dvnApp";



CREATE INDEX metadata_id_index
  ON metadata
  USING btree
  (id);


insert into metadata (
  id,
  citationrequirements,
  depositorrequirements,
  conditions,
  unf,
  disclaimer,
  title,
  productiondate,
  productionplace,
  fundingagency,
  confidentialitydeclaration,
  seriesinformation,
  distributiondate,
  timeperiodcoveredstart,
  distributorcontact,
  dateofcollectionstart,
  distributorcontactaffiliation,
  country,
  distributorcontactemail,
  geographicunit,
  depositor,
  universe,
  dateofdeposit,
  timemethod,
  frequencyofdatacollection,
  deviationsfromsampledesign,
  versiondate,
  researchinstrument,
  replicationfor,
  originofsources,
  subtitle,
  accesstosources,
  actionstominimizeloss,
  weighting,
  harvestholdings,
  studylevelerrornotes,
  harvestidentifier,
  samplingerrorestimate,
  harvestdvtermsofuse,
  placeofaccess,
  harvestdvntermsofuse,
  availabilitystatus,
  geographiccoverage,
  studycompletion,
  unitofanalysis,
  specialpermissions,
  kindofdata,
  contact,
  datacollector,
  samplingprocedure,
  collectionmode,
  seriesname,
  datasources,
  timeperiodcoveredend,
  characteristicofsources,
  datacollectionsituation,
  controloperations,
  cleaningoperations,
  dateofcollectionend,
  responserate,
  otherdataappraisal,
  restrictions,
  originalarchive,
  studyversion,
  collectionsize,
  version)
select
  id,
  citationrequirements,
  depositorrequirements,
  conditions,
  unf,
  disclaimer,
  title,
  productiondate,
  productionplace,
  fundingagency,
  confidentialitydeclaration,
  seriesinformation,
  distributiondate,
  timeperiodcoveredstart,
  distributorcontact,
  dateofcollectionstart,
  distributorcontactaffiliation,
  country,
  distributorcontactemail,
  geographicunit,
  depositor,
  universe,
  dateofdeposit,
  timemethod,
  frequencyofdatacollection,
  deviationsfromsampledesign,
  versiondate,
  researchinstrument,
  replicationfor,
  originofsources,
  subtitle,
  accesstosources,
  actionstominimizeloss,
  weighting,
  harvestholdings,
  studylevelerrornotes,
  harvestidentifier,
  samplingerrorestimate,
  harvestdvtermsofuse,
  placeofaccess,
  harvestdvntermsofuse,
  availabilitystatus,
  geographiccoverage,
  studycompletion,
  unitofanalysis,
  specialpermissions,
  kindofdata,
  contact,
  datacollector,
  samplingprocedure,
  collectionmode,
  seriesname,
  datasources,
  timeperiodcoveredend,
  characteristicofsources,
  datacollectionsituation,
  controloperations,
  cleaningoperations,
  dateofcollectionend,
  responserate,
  otherdataappraisal,
  restrictions,
  originalarchive,
  studyversion,
  collectionsize,
  version
 from study;
alter table metadata add CONSTRAINT metadata_pkey PRIMARY KEY (id);
-- StudyAbstract
ALTER TABLE studyabstract ADD COLUMN metadata_id int8;
ALTER TABLE studyabstract ALTER COLUMN metadata_id SET STORAGE PLAIN;
update studyabstract set metadata_id = study_id;
ALTER TABLE studyabstract
  ADD CONSTRAINT fk_studyabstract_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE studyabstract DROP COLUMN study_id;
ALTER TABLE studyabstract ALTER COLUMN metadata_id SET NOT NULL;

-- StudyAuthor
ALTER TABLE studyauthor ADD COLUMN metadata_id int8;
ALTER TABLE studyauthor ALTER COLUMN metadata_id SET STORAGE PLAIN;
update studyauthor set metadata_id = study_id;
ALTER TABLE studyauthor
  ADD CONSTRAINT fk_studyauthor_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE studyauthor DROP COLUMN study_id;
ALTER TABLE studyauthor ALTER COLUMN metadata_id SET NOT NULL;

-- StudyDistributor
ALTER TABLE studydistributor ADD COLUMN metadata_id int8;
ALTER TABLE studydistributor ALTER COLUMN metadata_id SET STORAGE PLAIN;
update studydistributor set metadata_id = study_id;
ALTER TABLE studydistributor
  ADD CONSTRAINT fk_studydistributor_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE studydistributor DROP COLUMN study_id;
ALTER TABLE studydistributor ALTER COLUMN metadata_id SET NOT NULL;

-- StudyGeoBounding
ALTER TABLE studygeobounding ADD COLUMN metadata_id int8;
ALTER TABLE studygeobounding ALTER COLUMN metadata_id SET STORAGE PLAIN;
update studygeobounding set metadata_id = study_id;
ALTER TABLE studygeobounding
  ADD CONSTRAINT fk_studygeobounding_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE studygeobounding DROP COLUMN study_id;
ALTER TABLE studygeobounding ALTER COLUMN metadata_id SET NOT NULL;

-- StudyGrant
ALTER TABLE studygrant ADD COLUMN metadata_id int8;
ALTER TABLE studygrant ALTER COLUMN metadata_id SET STORAGE PLAIN;
update studygrant set metadata_id = study_id;
ALTER TABLE studygrant
  ADD CONSTRAINT fk_studygrant_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE studygrant DROP COLUMN study_id;
ALTER TABLE studygrant ALTER COLUMN metadata_id SET NOT NULL;

-- StudyKeyword
ALTER TABLE studykeyword ADD COLUMN metadata_id int8;
ALTER TABLE studykeyword ALTER COLUMN metadata_id SET STORAGE PLAIN;
update studykeyword set metadata_id = study_id;
ALTER TABLE studykeyword
  ADD CONSTRAINT fk_studykeyword_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE studykeyword DROP COLUMN study_id;
ALTER TABLE studykeyword ALTER COLUMN metadata_id SET NOT NULL;

-- StudyNote
ALTER TABLE studynote ADD COLUMN metadata_id int8;
ALTER TABLE studynote ALTER COLUMN metadata_id SET STORAGE PLAIN;
update studynote set metadata_id = study_id;
ALTER TABLE studynote
  ADD CONSTRAINT fk_studynote_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE studynote DROP COLUMN study_id;
ALTER TABLE studynote ALTER COLUMN metadata_id SET NOT NULL;

-- StudyOtherId
ALTER TABLE studyotherid ADD COLUMN metadata_id int8;
ALTER TABLE studyotherid ALTER COLUMN metadata_id SET STORAGE PLAIN;
update studyotherid set metadata_id = study_id;
ALTER TABLE studyotherid
  ADD CONSTRAINT fk_studyotherid_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE studyotherid DROP COLUMN study_id;
ALTER TABLE studyotherid ALTER COLUMN metadata_id SET NOT NULL;

-- StudyOtherRef
ALTER TABLE studyotherref ADD COLUMN metadata_id int8;
ALTER TABLE studyotherref ALTER COLUMN metadata_id SET STORAGE PLAIN;
update studyotherref set metadata_id = study_id;
ALTER TABLE studyotherref
  ADD CONSTRAINT fk_studyotherref_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE studyotherref DROP COLUMN study_id;
ALTER TABLE studyotherref ALTER COLUMN metadata_id SET NOT NULL;

-- StudyProducer
ALTER TABLE studyproducer ADD COLUMN metadata_id int8;
ALTER TABLE studyproducer ALTER COLUMN metadata_id SET STORAGE PLAIN;
update studyproducer set metadata_id = study_id;
ALTER TABLE studyproducer
  ADD CONSTRAINT fk_studyproducer_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE studyproducer DROP COLUMN study_id;
ALTER TABLE studyproducer ALTER COLUMN metadata_id SET NOT NULL;

-- StudyRelMaterial
ALTER TABLE studyrelmaterial ADD COLUMN metadata_id int8;
ALTER TABLE studyrelmaterial ALTER COLUMN metadata_id SET STORAGE PLAIN;
update studyrelmaterial set metadata_id = study_id;
ALTER TABLE studyrelmaterial
  ADD CONSTRAINT fk_studyrelmaterial_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE studyrelmaterial DROP COLUMN study_id;
ALTER TABLE studyrelmaterial ALTER COLUMN metadata_id SET NOT NULL;

-- StudyRelPublication
ALTER TABLE studyrelpublication ADD COLUMN metadata_id int8;
ALTER TABLE studyrelpublication ALTER COLUMN metadata_id SET STORAGE PLAIN;
update studyrelpublication set metadata_id = study_id;
ALTER TABLE studyrelpublication
  ADD CONSTRAINT fk_studyrelpublication_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE studyrelpublication DROP COLUMN study_id;
ALTER TABLE studyrelpublication ALTER COLUMN metadata_id SET NOT NULL;

-- StudyRelStudy
ALTER TABLE studyrelstudy ADD COLUMN metadata_id int8;
ALTER TABLE studyrelstudy ALTER COLUMN metadata_id SET STORAGE PLAIN;
update studyrelstudy set metadata_id = study_id;
ALTER TABLE studyrelstudy
  ADD CONSTRAINT fk_studyrelstudy_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE studyrelstudy DROP COLUMN study_id;
ALTER TABLE studyrelstudy ALTER COLUMN metadata_id SET NOT NULL;

-- StudySoftware
ALTER TABLE studysoftware ADD COLUMN metadata_id int8;
ALTER TABLE studysoftware ALTER COLUMN metadata_id SET STORAGE PLAIN;
update studysoftware set metadata_id = study_id;
ALTER TABLE studysoftware
  ADD CONSTRAINT fk_studysoftware_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE studysoftware DROP COLUMN study_id;
ALTER TABLE studysoftware ALTER COLUMN metadata_id SET NOT NULL;

-- StudyTopicClass
ALTER TABLE studytopicclass ADD COLUMN metadata_id int8;
ALTER TABLE studytopicclass ALTER COLUMN metadata_id SET STORAGE PLAIN;
update studytopicclass set metadata_id = study_id;
ALTER TABLE studytopicclass
  ADD CONSTRAINT fk_studytopicclass_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE studytopicclass DROP COLUMN study_id;
ALTER TABLE studytopicclass ALTER COLUMN metadata_id SET NOT NULL;



-- Remove metadata from study
ALTER TABLE study DROP COLUMN    title;
ALTER TABLE study DROP COLUMN   citationrequirements;
ALTER TABLE study DROP COLUMN   depositorrequirements;
ALTER TABLE study DROP COLUMN   conditions;
ALTER TABLE study DROP COLUMN   unf;
ALTER TABLE study DROP COLUMN   disclaimer;
ALTER TABLE study DROP COLUMN   productiondate;
ALTER TABLE study DROP COLUMN   productionplace;
ALTER TABLE study DROP COLUMN   fundingagency;
ALTER TABLE study DROP COLUMN   confidentialitydeclaration;
ALTER TABLE study DROP COLUMN   seriesinformation;
ALTER TABLE study DROP COLUMN   distributiondate;
ALTER TABLE study DROP COLUMN   timeperiodcoveredstart;
ALTER TABLE study DROP COLUMN   distributorcontact;
ALTER TABLE study DROP COLUMN   dateofcollectionstart;
ALTER TABLE study DROP COLUMN   distributorcontactaffiliation;
ALTER TABLE study DROP COLUMN   country;
ALTER TABLE study DROP COLUMN   distributorcontactemail;
ALTER TABLE study DROP COLUMN   geographicunit;
ALTER TABLE study DROP COLUMN   depositor;
ALTER TABLE study DROP COLUMN   universe;
ALTER TABLE study DROP COLUMN   dateofdeposit;
ALTER TABLE study DROP COLUMN   timemethod;
ALTER TABLE study DROP COLUMN   frequencyofdatacollection;
ALTER TABLE study DROP COLUMN  deviationsfromsampledesign;
ALTER TABLE study DROP COLUMN   versiondate;
ALTER TABLE study DROP COLUMN   researchinstrument;
ALTER TABLE study DROP COLUMN   replicationfor;
ALTER TABLE study DROP COLUMN   originofsources;
ALTER TABLE study DROP COLUMN   subtitle;
ALTER TABLE study DROP COLUMN   accesstosources;
ALTER TABLE study DROP COLUMN    actionstominimizeloss;
ALTER TABLE study DROP COLUMN    weighting;
ALTER TABLE study DROP COLUMN    harvestholdings;
ALTER TABLE study DROP COLUMN    studylevelerrornotes;
ALTER TABLE study DROP COLUMN    harvestidentifier;
ALTER TABLE study DROP COLUMN    samplingerrorestimate;
ALTER TABLE study DROP COLUMN    harvestdvtermsofuse;
ALTER TABLE study DROP COLUMN    placeofaccess;
ALTER TABLE study DROP COLUMN    harvestdvntermsofuse;
ALTER TABLE study DROP COLUMN    availabilitystatus;
ALTER TABLE study DROP COLUMN    geographiccoverage;
ALTER TABLE study DROP COLUMN    studycompletion;
ALTER TABLE study DROP COLUMN    unitofanalysis;
ALTER TABLE study DROP COLUMN    specialpermissions;
ALTER TABLE study DROP COLUMN    kindofdata;
ALTER TABLE study DROP COLUMN    contact;
ALTER TABLE study DROP COLUMN    datacollector;
ALTER TABLE study DROP COLUMN    samplingprocedure;
ALTER TABLE study DROP COLUMN    collectionmode;
ALTER TABLE study DROP COLUMN    seriesname;
ALTER TABLE study DROP COLUMN    datasources;
ALTER TABLE study DROP COLUMN    timeperiodcoveredend;
ALTER TABLE study DROP COLUMN    characteristicofsources;
ALTER TABLE study DROP COLUMN    datacollectionsituation;
ALTER TABLE study DROP COLUMN    controloperations;
ALTER TABLE study DROP COLUMN    cleaningoperations;
ALTER TABLE study DROP COLUMN    dateofcollectionend;
ALTER TABLE study DROP COLUMN    responserate;
ALTER TABLE study DROP COLUMN    otherdataappraisal;
ALTER TABLE study DROP COLUMN    restrictions;
ALTER TABLE study DROP COLUMN   originalarchive;
ALTER TABLE study DROP COLUMN    studyversion;
ALTER TABLE study DROP COLUMN    collectionsize;




create index study_metadata_id_index on study(metadata_id);
create index studyabstract_metadata_id_index on studyabstract(metadata_id);
create index studyauthor_metadata_id_index on studyauthor(metadata_id);
create index studydistributor_metadata_id_index on studydistributor(metadata_id);
create index studygeobounding_metadata_id_index on studygeobounding(metadata_id);
create index studygrant_metadata_id_index on studygrant(metadata_id);
create index studykeyword_metadata_id_index on studykeyword(metadata_id);
create index studynote_metadata_id_index on studynote(metadata_id);
create index studyotherid_metadata_id_index on studyotherid(metadata_id);
create index studyotherref_metadata_id_index on studyotherref(metadata_id);
create index studyproducer_metadata_id_index on studyproducer(metadata_id);
create index studyrelmaterial_metadata_id_index on studyrelmaterial(metadata_id);
create index studyrelpublication_metadata_id_index on studyrelpublication(metadata_id);
create index studyrelstudy_metadata_id_index on studyrelstudy(metadata_id);
create index studysoftware_metadata_id_index on studysoftware(metadata_id);
create index studytopicclass_metadata_id_index on studytopicclass(metadata_id);

drop table vdc_template;
alter table template add column vdc_id int8;



alter table template add column metadata_id int8;


create index template_metadata_id_index on template(metadata_id);

ALTER TABLE study
  ADD CONSTRAINT fk_study_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE study ALTER COLUMN metadata_id SET NOT NULL;

alter table template add CONSTRAINT fk_template_vdc_id FOREIGN KEY (vdc_id)
      REFERENCES vdc (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

alter table template add CONSTRAINT fk_template_metadata_id FOREIGN KEY (metadata_id)
      REFERENCES metadata (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

 
 

SELECT setval('metadata_id_seq',100000);
INSERT INTO metadata(id, version ) VALUES (nextval('metadata_id_seq'), 1);
update template set metadata_id=currval('metadata_id_seq');
update template set name = 'Dataverse Network Default Template' where id=1;
SELECT setval('template_id_seq',10);

INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField) VALUES (96, '', 'producerAffiliation', FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField) VALUES (97, '', 'distributorAffiliation', FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField) VALUES (98, '', 'distributorAbbreviation', FALSE, FALSE, FALSE );

INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevel_id) VALUES(96,1,96,3);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevel_id) VALUES(97,1,97,3);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevel_id) VALUES(98,1,98,3);

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditTemplatePage','/study/EditTemplatePage.jsp',1,null );

-- Column: lastindextime

-- ALTER TABLE study DROP COLUMN lastindextime;

ALTER TABLE study ADD COLUMN lastindextime timestamp without time zone;
ALTER TABLE study ALTER COLUMN lastindextime SET STORAGE PLAIN;


ALTER TABLE datavariable ADD COLUMN formatcategory character varying(255);
ALTER TABLE datavariable ALTER COLUMN formatcategory SET STORAGE EXTENDED;

CREATE TABLE vdc_fileusergroup
(
  vdc_id int8 NOT NULL,
  allowedfilegroups_id int8 NOT NULL,
  CONSTRAINT vdc_fileusergroup_pkey PRIMARY KEY (vdc_id, allowedfilegroups_id),
  CONSTRAINT fk_vdc_fileusergroup_allowedfilegroups_id FOREIGN KEY (allowedfilegroups_id)
      REFERENCES usergroup (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_vdc_fileusergroup_vdc_id FOREIGN KEY (vdc_id)
      REFERENCES vdc (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITHOUT OIDS;
ALTER TABLE vdc_fileusergroup OWNER TO "dvnApp";

CREATE TABLE vdc_fileuser
(
  vdc_id int8 NOT NULL,
  allowedfileusers_id int8 NOT NULL,
  CONSTRAINT vdc_vdc_fileuser_pkey PRIMARY KEY (vdc_id, allowedfileusers_id),
  CONSTRAINT fk_vdc_vdcuser_allowedfileusers_id FOREIGN KEY (allowedfileusers_id)
      REFERENCES vdcuser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_vdc_fileuser_vdc_id FOREIGN KEY (vdc_id)
      REFERENCES vdc (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITHOUT OIDS;
ALTER TABLE vdc_fileuser OWNER TO "dvnApp";

ALTER TABLE vdc ADD COLUMN filesrestricted bool;
ALTER TABLE vdc ALTER COLUMN filesrestricted SET STORAGE PLAIN;


ALTER TABLE harvestingdataverse ADD COLUMN harvesttype character varying(255);
ALTER TABLE harvestingdataverse ALTER COLUMN harvesttype SET STORAGE EXTENDED;


ALTER TABLE datavariable ADD COLUMN numberofdecimalpoints bigint;
ALTER TABLE datavariable ALTER COLUMN numberofdecimalpoints SET STORAGE PLAIN;

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'TermsOfUsePage','/login/TermsOfUsePage.jsp', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'AccountTermsOfUsePage','/login/AccountTermsOfUsePage.jsp', null,null );

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'PromotionalLinkSearchBoxPage','/admin/PromotionalLinkSearchBoxPage.jsp', 3,null );

SELECT setval('templatefield_id_seq',200);
ALTER TABLE study ADD COLUMN harvestidentifier varchar(255);
ALTER TABLE study ALTER COLUMN harvestidentifier SET STORAGE PLAIN;

update study set harvestidentifier = (select harvestidentifier from metadata where metadata.id = study.metadata_id);
alter table metadata drop column harvestidentifier;


-- copy filesrestricted values from harvestingdataverse (and relationship tables)

update vdc
set filesrestricted = hd.filesrestricted
from harvestingdataverse hd
where harvestingdataverse_id = hd.id;

insert into vdc_fileuser
	select vdc.id, allowedfileusers_id
	from vdc, harvestingdataverse hd, harvestingdataverse_vdcuser hdu
	where vdc.harvestingdataverse_id = hd.id
	and hd.id = hdu.harvestingdataverse_id;
	
insert into vdc_fileusergroup
	select vdc.id, allowedfilegroups_id
	from vdc, harvestingdataverse hd, harvestingdataverse_usergroup hdug
	where vdc.harvestingdataverse_id = hd.id
	and hd.id = hdug.harvestingdataverse_id;

update vdc set filesrestricted=false where harvestingdataverse_id is null;

-- remove unused columns
alter table harvestingdataverse drop column filesrestricted;
alter table harvestingdataverse drop column format;

update harvestingdataverse set harvesttype='oai';


commit;



