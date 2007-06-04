--DB Changes for Beta_3 :


ALTER TABLE vdc ADD COLUMN harvestingdataverse_id int8;
ALTER TABLE vdc ALTER COLUMN harvestingdataverse_id SET STORAGE PLAIN;

ALTER TABLE datavariable ADD COLUMN fileorder int4;

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditHarvestSitePage', '/site/EditHarvestSitePage.jsp', null,1 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'HarvestSitesPage', '/site/HarvestSitesPage.jsp', null,1 );
