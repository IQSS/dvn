-- changes for default header and footer
update vdcnetwork set defaultvdcheader='<style type="text/css">
body {margin:0; padding:0;}
</style>
<div style="width:100%; height:40px; background: url(/dvn/resources/images/customizationpattern.png) repeat-x left -35px #698DA2;"></div>
<div style="margin:0 auto; max-width:1000px;">';

update vdcnetwork set defaultvdcfooter='</div>';

-- removal of default description (replaced by new logic)
update vdcnetwork set defaultvdcannouncements = '';

update vdc  set announcements = ''
where announcements like 'A description of your Dataverse or announcements may be added here.%';


-- Changes for new template logic
Alter table studyField add COLUMN customfield boolean;
update studyField set customfield = false;

Alter table studyField add COLUMN fieldType character varying(255);

alter table StudyField add ALLOWMULTIPLES boolean;

ALTER TABLE StudyField ADD COLUMN parentstudyfield_id bigint;
ALTER TABLE StudyField ALTER COLUMN parentstudyfield_id SET STORAGE PLAIN;

-- replace primary study fields with parents
update studyfield set name = 'author' where name = 'authorName';
update studyfield set name = 'producer' where name = 'producerName';
update studyfield set name = 'distributor' where name = 'distributorName';
update studyfield set name = 'description' where name = 'abstractText';
update studyfield set name = 'keyword' where name = 'keywordValue';
update studyfield set name = 'topicClassification' where name = 'topicClassValue';
update studyfield set name = 'software' where name = 'softwareName';
update studyfield set name = 'series' where name = 'seriesName';
update studyfield set name = 'version' where name = 'studyVersion';
update studyfield set name = 'geographicBoundingBox' where name = 'westLongitude';
update studyfield set name = 'note' where name = 'notesInformationType';
update studyfield set name = 'publication' where name = 'relatedPublications';
update studyfield set name = 'publicationReplicationData' where name = 'replicationFor';

-- now re add primary fields and a few new
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (99, '', '', 'authorName', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (100, '', '', 'producerName', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (101, '', '', 'distributorName', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (102, '', '', 'distributorContactName', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (103, '', '', 'descriptionText', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (104, '', '', 'keywordValue', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (105, '', '', 'topicClassValue', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (106, '', '', 'otherIdValue', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (107, '', '', 'softwareName', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (108, '', '', 'grantNumberValue', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (109, '', '', 'seriesName', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (110, '', '', 'studyVersionValue', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (111, '', '', 'westLongitutde', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (112, '', '', 'noteInformationType', FALSE, FALSE, FALSE, FALSE );

INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (113, '', '', 'publicationCitation', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (114, '', '', 'publicationIDType', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (115, '', '', 'publicationIDNumber', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (116, '', '', 'publicationURL', FALSE, FALSE, FALSE, FALSE );

-- set parent / child relatinships
update stuyfield set parentstudyfield_id = 3 where id = 99;
update stuyfield set parentstudyfield_id = 3 where id = 4;

update stuyfield set parentstudyfield_id = 5 where id = 100;
update stuyfield set parentstudyfield_id = 5 where id = 6;
update stuyfield set parentstudyfield_id = 5 where id = 7;
update stuyfield set parentstudyfield_id = 5 where id = 8;
update stuyfield set parentstudyfield_id = 5 where id = 96;

update stuyfield set parentstudyfield_id = 15 where id = 101;
update stuyfield set parentstudyfield_id = 15 where id = 16;
update stuyfield set parentstudyfield_id = 15 where id = 17;
update stuyfield set parentstudyfield_id = 15 where id = 97;
update stuyfield set parentstudyfield_id = 15 where id = 98;

update stuyfield set parentstudyfield_id = 19 where id = 102;
update stuyfield set parentstudyfield_id = 19 where id = 20;
update stuyfield set parentstudyfield_id = 19 where id = 21;

update stuyfield set parentstudyfield_id = 33 where id = 103;
update stuyfield set parentstudyfield_id = 33 where id = 34;

update stuyfield set parentstudyfield_id = 27 where id = 104;
update stuyfield set parentstudyfield_id = 27 where id = 28;
update stuyfield set parentstudyfield_id = 27 where id = 29;

update stuyfield set parentstudyfield_id = 30 where id = 105;
update stuyfield set parentstudyfield_id = 30 where id = 31;
update stuyfield set parentstudyfield_id = 30 where id = 32;

update stuyfield set parentstudyfield_id = 85 where id = 106;
update stuyfield set parentstudyfield_id = 85 where id = 86;

update stuyfield set parentstudyfield_id = 10 where id = 107;
update stuyfield set parentstudyfield_id = 10 where id = 11;

update stuyfield set parentstudyfield_id = 13 where id = 108;
update stuyfield set parentstudyfield_id = 13 where id = 14;

update stuyfield set parentstudyfield_id = 24 where id = 109;
update stuyfield set parentstudyfield_id = 24 where id = 25;

update stuyfield set parentstudyfield_id = 26 where id = 110;
update stuyfield set parentstudyfield_id = 26 where id = 91;

update stuyfield set parentstudyfield_id = 92 where id = 111;
update stuyfield set parentstudyfield_id = 92 where id = 93;
update stuyfield set parentstudyfield_id = 92 where id = 94;
update stuyfield set parentstudyfield_id = 92 where id = 95;

update stuyfield set parentstudyfield_id = 83 where id = 112;
update stuyfield set parentstudyfield_id = 83 where id = 82;
update stuyfield set parentstudyfield_id = 83 where id = 84;

update stuyfield set parentstudyfield_id = 79 where id = 113;
update stuyfield set parentstudyfield_id = 79 where id = 114;
update stuyfield set parentstudyfield_id = 79 where id = 115;
update stuyfield set parentstudyfield_id = 79 where id = 116;
update stuyfield set parentstudyfield_id = 79 where id = 89;




update templatefield set version = 1 where version is null;

alter table templateField ADD displayorder  int;
update templatefield set displayorder = -1;

ALTER TABLE templatefield ADD COLUMN controlledvocabulary_id bigint;
ALTER TABLE templatefield ALTER COLUMN controlledvocabulary_id SET STORAGE PLAIN;


Alter table templatefield add fieldInputLevelString character varying(255);
UPDATE templatefield SET fieldInputLevelString='required' WHERE fieldinputlevel_id =1;
UPDATE templatefield SET fieldInputLevelString='recommended'WHERE fieldinputlevel_id =2;
UPDATE templatefield SET fieldInputLevelString='optional' WHERE fieldinputlevel_id =3;
--We need to keep drop column query at the end because the data it contains is used to fill the new String field.
ALTER TABLE templatefield DROP COLUMN fieldinputlevel_id;


alter table template ADD COLUMN enabled boolean;
update template set enabled=true;

-- insert template fields for all the new fields
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'authorName';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'producerName';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'distributorName';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'distributorContactName';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'descriptionText';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'keywordValue';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'topicClassValue';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'otherIdValue';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'softwareName';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'grantNumberValue';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'seriesName';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'studyVersionValue';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'westLongitude';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'noteInformationType';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'publicationCitation';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'publicationIDType';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'publicationIDNumber';
INSERT INTO templatefield(version, template_id, studyfield_id, fieldinputlevelstring, displayorder) select 1,template.id, studyfield.id, 'optional', -1 from template, studyfield where studyfield.name = 'publicationURL';



-- Page def permission changes:
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManageControlledVocabularyPage', '/admin/ManageControlledVocabularyPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManageTemplatesPage', '/admin/ManageTemplatesPage.xhtml', 2,2 );
update pagedef set role_id=2,networkrole_id=2 where name = 'TemplateFormPage';


-- Author related changes
Alter table studyAuthor add IDType  character varying(255);
Alter table studyAuthor add IDValue  character varying(255);


-- Adding mimetype column to the datafileformattype table
ALTER TABLE DataFileFormatType ADD COLUMN mimeType character varying(255);
UPDATE DataFileFormatType SET mimeType='application/x-rlang-transport' WHERE id=1;
UPDATE DataFileFormatType SET mimeType='application/x-stata' WHERE id=2;
UPDATE DataFileFormatType SET mimeType='application/x-R-2' WHERE id=3;

-- Adding the DVN-wide Terms of Use Authorization to the VDCUser table
ALTER TABLE VDCUser ADD COLUMN BypassTermsOfUse boolean;

INSERT INTO metadataformattype (id, name, mimetype, namespace, formatschema, partialexcludesupported, partialselectsupported) VALUES (1, 'ddi', 'application/xml', 'http://www.icpsr.umich.edu/DDI', 'http://www.icpsr.umich.edu/DDI/Version2-0.xsd', true, true);
INSERT INTO metadataformattype (id, name, mimetype, namespace, formatschema, partialexcludesupported, partialselectsupported) VALUES (2, 'oai_dc', 'application/xml', 'http://www.openarchives.org/OAI/2.0/oai_dc/', 'http://www.openarchives.org/OAI/2.0/oai_dc.xsd', false, false);
INSERT INTO metadataformattype (id, name, mimetype, namespace, formatschema, partialexcludesupported, partialselectsupported) VALUES (3, 'marc', 'application/octet-stream', 'http://www.loc.gov/marc/', 'MARC 21', false, false);

--Update of Notes related study fields
UPDATE studyfield set name = 'notesInformationType' where name = 'NotesInformationType';
UPDATE studyfield set name = 'notesInformationSubject' where name = 'NotesInformationSubject';
UPDATE studyfield set name = 'notesText' where name = 'NotesText';
UPDATE studyfield set title=description WHERE description != '';

--new columns to related publications
ALTER table StudyRelPublication ADD COLUMN idType character varying(255);
ALTER table StudyRelPublication ADD COLUMN idNumber character varying(255);
ALTER table StudyRelPublication ADD COLUMN url character varying(255);
ALTER table StudyRelPublication ADD COLUMN replicationData boolean;
UPDATE StudyRelPublication SET replicationData=false, displayorder = displayorder +1;

-- move old replication for values to study publication table
insert into studyrelpublication (metadata_id, version, text,displayOrder,replicationdata) select id, 1, replicationfor, 0, true from metadata where replicationfor != '';

-- drop old table(s)
drop table templatefieldselectvalue;