ALTER TABLE studygrant ALTER COLUMN number type text;
ALTER TABLE studygrant ALTER COLUMN number SET STORAGE EXTENDED;

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ForgotPasswordPage', '/login/ForgotPasswordPage.jsp', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'UtilitiesPage', '/networkAdmin/UtilitiesPage.jsp', null, 2 );
INSERT INTO pagedef (name, path, role_id, networkrole_id ) VALUES  ( 'SubsettingPage', '/subsetting/SubsettingPage.jsp', null,null );

update dvnversion set versionnumber=1, buildnumber=2;

DELETE FROM pagedef where name= 'EditBannerContactUsPage';

ALTER TABLE pagedef
  ADD CONSTRAINT unq_pagedef_0 UNIQUE(path);

ALTER TABLE pagedef
  ADD CONSTRAINT unq_pagedef_1 UNIQUE(name);

ALTER TABLE harvestingdataverse ADD COLUMN generaterandomids bool;
ALTER TABLE harvestingdataverse ALTER COLUMN generaterandomids SET STORAGE PLAIN;

UPDATE harvestingdataverse SET generaterandomids = false;



