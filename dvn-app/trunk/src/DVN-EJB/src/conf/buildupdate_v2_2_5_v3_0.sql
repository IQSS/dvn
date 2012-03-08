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

CREATE TABLE metadataformattype (ID BIGINT NOT NULL, FORMATSCHEMA VARCHAR(255), MIMETYPE VARCHAR(255), NAME VARCHAR(255), NAMESPACE VARCHAR(255), PARTIALEXCLUDESUPPORTED BOOLEAN, PARTIALSELECTSUPPORTED BOOLEAN, PRIMARY KEY (ID));
INSERT INTO metadataformattype (id, name, mimetype, namespace, formatschema, partialexcludesupported, partialselectsupported) VALUES (1, 'ddi', 'application/xml', 'http://www.icpsr.umich.edu/DDI', 'http://www.icpsr.umich.edu/DDI/Version2-0.xsd', true, true);
INSERT INTO metadataformattype (id, name, mimetype, namespace, formatschema, partialexcludesupported, partialselectsupported) VALUES (2, 'oai_dc', 'application/xml', 'http://www.openarchives.org/OAI/2.0/oai_dc/', 'http://www.openarchives.org/OAI/2.0/oai_dc.xsd', false, false);
INSERT INTO metadataformattype (id, name, mimetype, namespace, formatschema, partialexcludesupported, partialselectsupported) VALUES (3, 'marc', 'application/octet-stream', 'http://www.loc.gov/marc/', 'MARC 21', false, false);

--Update of Notes related study fields
UPDATE studyfield set name = 'notesInformationType' where name = 'NotesInformationType';
UPDATE studyfield set name = 'notesInformationSubject' where name = 'NotesInformationSubject';
UPDATE studyfield set name = 'notesText' where name = 'NotesText';

--new columns to related publications
ALTER table StudyRelPublication ADD COLUMN idType character varying(255);
ALTER table StudyRelPublication ADD COLUMN idNumber character varying(255);
ALTER table StudyRelPublication ADD COLUMN url character varying(255);
ALTER table StudyRelPublication ADD COLUMN replicationData boolean;
UPDATE StudyRelPublication SET replicationData=false, displayorder = displayorder +1;

-- move old replication for values to study publication table
insert into studyrelpublication (metadata_id, version, text,displayOrder,replicationdata) select id, 1, replicationfor, 0, true from metadata where replicationfor != '';