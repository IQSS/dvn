
--Add field type to controlled Vocabulary for validation purposes
ALTER TABLE controlledVocabulary
 ADD fieldType character varying(255);

--update display order for setting controlled vocab
--(so that the popup matches the order in the template form)
update studyfield set displayorder = 1 where name = 'authorName';
update studyfield set displayorder = 1 where name = 'authorName';
update studyfield set displayorder = 2 where name = 'authorAffiliation';
update studyfield set displayorder = 2 where name = 'producerAbbreviation';
update studyfield set displayorder = 1 where name = 'producerName';
update studyfield set displayorder = 3 where name = 'producerAffiliation';
update studyfield set displayorder = 4 where name = 'producerURL';
update studyfield set displayorder = 5 where name = 'producerLogo';
update studyfield set displayorder = 2 where name = 'softwareVersion';
update studyfield set displayorder = 1 where name = 'softwareName';
update studyfield set displayorder = 1 where name = 'grantNumberValue';
update studyfield set displayorder = 2 where name = 'grantNumberAgency';
update studyfield set displayorder = 1 where name = 'distributorName';
update studyfield set displayorder = 4 where name = 'distributorURL';
update studyfield set displayorder = 5 where name = 'distributorLogo';
update studyfield set displayorder = 3 where name = 'distributorAffiliation';
update studyfield set displayorder = 2 where name = 'distributorAbbreviation';
update studyfield set displayorder = 1 where name = 'distributorContactName';
update studyfield set displayorder = 2 where name = 'distributorContactAffiliation';
update studyfield set displayorder = 3 where name = 'distributorContactEmail';
update studyfield set displayorder = 2 where name = 'seriesInformation';
update studyfield set displayorder = 1 where name = 'seriesName';
update studyfield set displayorder = 1 where name = 'studyVersionValue';
update studyfield set displayorder = 2 where name = 'versionDate';
update studyfield set displayorder = 1 where name = 'keywordValue';
update studyfield set displayorder = 3 where name = 'keywordVocabURI';
update studyfield set displayorder = 2 where name = 'keywordVocab';
update studyfield set displayorder = 1 where name = 'topicClassValue';
update studyfield set displayorder = 2 where name = 'topicClassVocab';
update studyfield set displayorder = 3 where name = 'topicClassVocabURI';
update studyfield set displayorder = 1 where name = 'descriptionText';
update studyfield set displayorder = 2 where name = 'descriptionDate';
update studyfield set displayorder = 1 where name = 'publicationCitation';
update studyfield set displayorder = 2 where name = 'publicationIDNumber';
update studyfield set displayorder = 3 where name = 'publicationURL';
update studyfield set displayorder = 3 where name = 'notesText';
update studyfield set displayorder = 1 where name = 'noteInformationType';
update studyfield set displayorder = 2 where name = 'notesInformationSubject';
update studyfield set displayorder = 2 where name = 'otherIdAgency';
update studyfield set displayorder = 1 where name = 'otherIdValue';

--Add field type to studyfield table 
--so that CV can be validated
update studyfield set fieldtype = 'date' where id = 9;
update studyfield set fieldtype = 'date' where id = 18;
update studyfield set fieldtype = 'date' where id = 23;
update studyfield set fieldtype = 'date' where id = 34;
update studyfield set fieldtype = 'date' where id = 35;
update studyfield set fieldtype = 'date' where id = 36;
update studyfield set fieldtype = 'date' where id = 37;
update studyfield set fieldtype = 'date' where id = 38;
update studyfield set fieldtype = 'date' where id = 91;
update studyfield set fieldtype = 'email' where id = 21;
update studyfield set fieldtype = 'textBox' where id = 4;
update studyfield set fieldtype = 'textBox' where id = 8;
update studyfield set fieldtype = 'textBox' where id = 11;
update studyfield set fieldtype = 'textBox' where id = 12;
update studyfield set fieldtype = 'textBox' where id = 13;
update studyfield set fieldtype = 'textBox' where id = 14;
update studyfield set fieldtype = 'textBox' where id = 19;
update studyfield set fieldtype = 'textBox' where id = 20;
update studyfield set fieldtype = 'textBox' where id = 22;
update studyfield set fieldtype = 'textBox' where id = 24;
update studyfield set fieldtype = 'textBox' where id = 25;
update studyfield set fieldtype = 'textBox' where id = 26;
update studyfield set fieldtype = 'textBox' where id = 27;
update studyfield set fieldtype = 'textBox' where id = 28;
update studyfield set fieldtype = 'textBox' where id = 30;
update studyfield set fieldtype = 'textBox' where id = 31;
update studyfield set fieldtype = 'textBox' where id = 33;
update studyfield set fieldtype = 'textBox' where id = 39;
update studyfield set fieldtype = 'textBox' where id = 40;
update studyfield set fieldtype = 'textBox' where id = 41;
update studyfield set fieldtype = 'textBox' where id = 42;
update studyfield set fieldtype = 'textBox' where id = 43;
update studyfield set fieldtype = 'textBox' where id = 44;
update studyfield set fieldtype = 'textBox' where id = 45;
update studyfield set fieldtype = 'textBox' where id = 46;
update studyfield set fieldtype = 'textBox' where id = 47;
update studyfield set fieldtype = 'textBox' where id = 48;
update studyfield set fieldtype = 'textBox' where id = 49;
update studyfield set fieldtype = 'textBox' where id = 50;
update studyfield set fieldtype = 'textBox' where id = 51;
update studyfield set fieldtype = 'textBox' where id = 52;
update studyfield set fieldtype = 'textBox' where id = 53;
update studyfield set fieldtype = 'textBox' where id = 54;
update studyfield set fieldtype = 'textBox' where id = 55;
update studyfield set fieldtype = 'textBox' where id = 56;
update studyfield set fieldtype = 'textBox' where id = 57;
update studyfield set fieldtype = 'textBox' where id = 58;
update studyfield set fieldtype = 'textBox' where id = 59;
update studyfield set fieldtype = 'textBox' where id = 60;
update studyfield set fieldtype = 'textBox' where id = 61;
update studyfield set fieldtype = 'textBox' where id = 62;
update studyfield set fieldtype = 'textBox' where id = 63;
update studyfield set fieldtype = 'textBox' where id = 64;
update studyfield set fieldtype = 'textBox' where id = 65;
update studyfield set fieldtype = 'textBox' where id = 66;
update studyfield set fieldtype = 'textBox' where id = 67;
update studyfield set fieldtype = 'textBox' where id = 68;
update studyfield set fieldtype = 'textBox' where id = 69;
update studyfield set fieldtype = 'textBox' where id = 70;
update studyfield set fieldtype = 'textBox' where id = 71;
update studyfield set fieldtype = 'textBox' where id = 72;
update studyfield set fieldtype = 'textBox' where id = 73;
update studyfield set fieldtype = 'textBox' where id = 74;
update studyfield set fieldtype = 'textBox' where id = 75;
update studyfield set fieldtype = 'textBox' where id = 76;
update studyfield set fieldtype = 'textBox' where id = 77;
update studyfield set fieldtype = 'textBox' where id = 78;
update studyfield set fieldtype = 'textBox' where id = 79;
update studyfield set fieldtype = 'textBox' where id = 80;
update studyfield set fieldtype = 'textBox' where id = 81;
update studyfield set fieldtype = 'textBox' where id = 82;
update studyfield set fieldtype = 'textBox' where id = 83;
update studyfield set fieldtype = 'textBox' where id = 84;
update studyfield set fieldtype = 'textBox' where id = 85;
update studyfield set fieldtype = 'textBox' where id = 86;
update studyfield set fieldtype = 'textBox' where id = 87;
update studyfield set fieldtype = 'textBox' where id = 88;
update studyfield set fieldtype = 'textBox' where id = 89;
update studyfield set fieldtype = 'textBox' where id = 92;
update studyfield set fieldtype = 'textBox' where id = 96;
update studyfield set fieldtype = 'textBox' where id = 97;
update studyfield set fieldtype = 'textBox' where id = 98;
update studyfield set fieldtype = 'textBox' where id = 99;
update studyfield set fieldtype = 'textBox' where id = 100;
update studyfield set fieldtype = 'textBox' where id = 101;
update studyfield set fieldtype = 'textBox' where id = 102;
update studyfield set fieldtype = 'textBox' where id = 103;
update studyfield set fieldtype = 'textBox' where id = 104;
update studyfield set fieldtype = 'textBox' where id = 105;
update studyfield set fieldtype = 'textBox' where id = 106;
update studyfield set fieldtype = 'textBox' where id = 107;
update studyfield set fieldtype = 'textBox' where id = 108;
update studyfield set fieldtype = 'textBox' where id = 109;
update studyfield set fieldtype = 'textBox' where id = 110;
update studyfield set fieldtype = 'textBox' where id = 112;
update studyfield set fieldtype = 'textBox' where id = 113;
update studyfield set fieldtype = 'textBox' where id = 115;
update studyfield set fieldtype = 'url' where id = 6;
update studyfield set fieldtype = 'url' where id = 7;
update studyfield set fieldtype = 'url' where id = 16;
update studyfield set fieldtype = 'url' where id = 17;
update studyfield set fieldtype = 'url' where id = 29;
update studyfield set fieldtype = 'url' where id = 32;
update studyfield set fieldtype = 'url' where id = 116;

--Ticket 2513 - allow custom field names to be more than 255 characters
ALTER TABLE studyfield ALTER COLUMN "name" TYPE text;
ALTER TABLE studyfield ALTER COLUMN "name" SET STORAGE EXTENDED;

ALTER TABLE studyfield ALTER COLUMN "title" TYPE text;
ALTER TABLE studyfield ALTER COLUMN "title" SET STORAGE EXTENDED;

ALTER TABLE studyfield ALTER COLUMN "description" TYPE text;
ALTER TABLE studyfield ALTER COLUMN "description" SET STORAGE EXTENDED;

--Ticket 2566 - add Read Only Mode 
ALTER TABLE vdcNetwork
 ADD readonly boolean;

ALTER TABLE vdcNetwork
 ADD statusnotice  text;

ALTER TABLE vdcNetwork ALTER COLUMN "statusnotice" SET STORAGE EXTENDED;