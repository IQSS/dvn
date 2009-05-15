
--
-- first do DDL (this cannot be rollbacked)
--

-- changes to vdc for study comments
ALTER TABLE vdc ADD COLUMN allowstudycomments bool default true;
ALTER TABLE vdc ALTER COLUMN allowstudycomments SET STORAGE PLAIN;

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditStudyCommentsPage', '/admin/EditStudyCommentsPage.xhtml', 3,null );

--
-- and now DML
--
begin;


commit;
