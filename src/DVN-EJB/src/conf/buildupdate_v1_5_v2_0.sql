
--
-- first do DDL (this cannot be rollbacked)
--

-- changes to vdc for study comments
ALTER TABLE vdc ADD COLUMN commentnotificationemail varchar(255);
ALTER TABLE vdc ALTER COLUMN commentnotificationemail SET STORAGE EXTENDED;

ALTER TABLE vdc ADD COLUMN allowstudycomments bool;
ALTER TABLE vdc ALTER COLUMN allowstudycomments SET STORAGE PLAIN;

ALTER TABLE vdc ADD COLUMN generatenotifications bool;
ALTER TABLE vdc ALTER COLUMN generatenotifications SET STORAGE PLAIN;


--
-- and now DML
--
begin;


update dvnversion set buildnumber=0, versionnumber=2;
update vdc set allowstudycomments=true;
update vdc set generatenotifications=true;

commit;
