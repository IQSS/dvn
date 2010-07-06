ALTER TABLE datavariable ADD COLUMN version int8;
ALTER TABLE datavariable ALTER COLUMN version SET STORAGE PLAIN;

update datavariable set version=1;

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditVariablePage','/study/EditVariablePage.jsp',1,null );

ALTER TABLE vdc ADD COLUMN downloadtermsofuseenabled bool;
ALTER TABLE vdc ALTER COLUMN downloadtermsofuseenabled SET STORAGE PLAIN;

ALTER TABLE vdc ADD COLUMN downloadtermsofuse text;
ALTER TABLE vdc ALTER COLUMN downloadtermsofuse SET STORAGE EXTENDED;

update vdc set downloadtermsofuseenabled=termsofuseenabled;
update vdc set downloadtermsofuse=termsofuse;

update vdc set termsofuseenabled=false;
update vdc set termsofuse='';

ALTER TABLE vdcnetwork ADD COLUMN termsofuseenabled bool;
ALTER TABLE vdcnetwork ALTER COLUMN termsofuseenabled SET STORAGE PLAIN;
update vdcnetwork set termsofuseenabled=false;
