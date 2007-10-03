ALTER TABLE vdcnetwork ADD COLUMN exporthourofday int4;
ALTER TABLE vdcnetwork ALTER COLUMN exporthourofday SET STORAGE PLAIN;

ALTER TABLE vdcnetwork ADD COLUMN exportdayofweek int4;
ALTER TABLE vdcnetwork ALTER COLUMN exportdayofweek SET STORAGE PLAIN;

ALTER TABLE vdcnetwork ADD COLUMN exportperiod varchar(255);
ALTER TABLE vdcnetwork ALTER COLUMN exportperiod SET STORAGE EXTENDED;

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditExportSchedulePage', '/networkAdmin/EditExportSchedulePage.jsp', null,2 );

ALTER TABLE study ADD COLUMN lastexporttime timestamp;
ALTER TABLE study ALTER COLUMN lastexporttime SET STORAGE PLAIN;



