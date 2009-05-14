
--
-- first do DDL (this cannot be rollbacked)
--

-- changes to vdc for study comments
ALTER TABLE vdc ADD COLUMN allowstudycomments bool default true;
ALTER TABLE vdc ALTER COLUMN allowstudycomments SET STORAGE PLAIN;

--
-- and now DML
--
begin;


update vdc set allowstudycomments=true;

commit;
