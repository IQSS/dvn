update vdcnetwork set id = 0 where id = 1;
Alter TABLE vdc add vdcnetwork_id bigint;

Update vdc set vdcnetwork_id = 0;

ALTER TABLE vdcnetwork ADD COLUMN urlalias character varying(255);
ALTER TABLE vdcnetwork ALTER COLUMN urlalias SET STORAGE EXTENDED;

ALTER TABLE vdcnetwork ADD COLUMN networkcreated timestamp;
ALTER TABLE vdcnetwork ALTER COLUMN networkcreated SET STORAGE PLAIN;

ALTER TABLE vdcnetwork ADD COLUMN curator character varying(255);
ALTER TABLE vdcnetwork ALTER COLUMN curator SET STORAGE EXTENDED;

ALTER TABLE vdcnetwork ADD COLUMN creator_id bigint;
ALTER TABLE vdcnetwork ALTER COLUMN creator_id SET STORAGE PLAIN;

ALTER TABLE vdcnetwork ADD COLUMN released boolean;

Alter TABLE template add vdcnetwork_id bigint;

Update template set vdcnetwork_id = 0;

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditSubnetworkPage', '/networkAdmin/EditSubnetworkPage.xhtml', null, 2 );

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManageSubnetworksPage', '/networkAdmin/ManageSubnetworksPage.xhtml', null, 2 );