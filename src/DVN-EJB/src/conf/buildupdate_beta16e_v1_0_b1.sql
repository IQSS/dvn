ALTER TABLE datavariable ADD COLUMN version int8;
ALTER TABLE datavariable ALTER COLUMN version SET STORAGE PLAIN;

update datavariable set version=1;

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditVariablePage','/study/EditVariablePage.jsp',1,null );
