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
Alter table studyField add COLUMN dcmField boolean;
update studyField set dcmField = false;

Alter table studyField add COLUMN fieldType character varying(255);

alter table StudyField add ALLOWMULTIPLES boolean;

ALTER TABLE StudyField ADD COLUMN FieldInputLevelString character varying(255);
UPDATE StudyField SET FieldInputLevelString='required' WHERE id=1;
UPDATE StudyField SET FieldInputLevelString='required' WHERE id=2;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=3;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=4;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=5;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=6;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=7;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=8;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=9;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=10;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=11;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=12;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=13;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=14;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=15;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=16;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=17;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=18;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=19;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=20;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=21;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=22;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=23;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=24;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=25;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=26;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=27;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=28;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=29;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=30;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=31;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=32;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=33;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=34;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=35;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=36;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=37;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=38;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=39;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=40;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=41;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=42;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=43;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=44;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=45;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=46;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=47;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=48;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=49;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=50;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=51;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=52;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=53;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=54;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=55;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=56;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=57;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=58;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=59;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=60;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=61;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=62;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=63;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=64;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=65;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=66;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=67;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=68;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=69;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=70;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=71;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=72;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=73;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=74;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=75;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=76;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=77;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=78;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=79;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=80;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=81;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=82;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=83;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=84;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=85;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=86;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=87;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=88;
UPDATE StudyField SET FieldInputLevelString='recommended' WHERE id=89;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=90;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=91;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=92;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=93;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=94;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=95;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=96;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=97;
UPDATE StudyField SET FieldInputLevelString='optional' WHERE id=98;


update templatefield set version = 1 where version is null;

alter table templateField ADD dcmSortOrder  int;
update templatefield set dcmsortorder = studyfield_id - 44 where studyfield_id > 44 and studyfield_id < 65;

alter table template ADD status character varying(255);


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

CREATE TABLE metadataformattype (ID BIGINT NOT NULL, FORMATSCHEMA VARCHAR(255), MIMETYPE VARCHAR(255), NAME VARCHAR(255), NAMESPACE VARCHAR(255), PARTIALEXCLUDESUPPORTED BOOLEAN, PARTIALSELECTSUPPORTED BOOLEAN, PRIMARY KEY (ID))
INSERT INTO metadataformattype (id, name, mimetype, namespace, formatschema, partialexcludesupported, partialselectsupported) VALUES (1, 'ddi', 'application/xml', 'http://www.icpsr.umich.edu/DDI', 'http://www.icpsr.umich.edu/DDI/Version2-0.xsd', true, true);
INSERT INTO metadataformattype (id, name, mimetype, namespace, formatschema, partialexcludesupported, partialselectsupported) VALUES (2, 'oai_dc', 'application/xml', 'http://www.openarchives.org/OAI/2.0/oai_dc/', 'http://www.openarchives.org/OAI/2.0/oai_dc.xsd', false, false);
INSERT INTO metadataformattype (id, name, mimetype, namespace, formatschema, partialexcludesupported, partialselectsupported) VALUES (3, 'marc', 'application/octet-stream', 'http://www.loc.gov/marc/', 'MARC 21', false, false);
