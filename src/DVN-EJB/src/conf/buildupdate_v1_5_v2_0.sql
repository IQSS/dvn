
--
-- first do DDL (this cannot be rollbacked)
--

-- changes to vdc for study comments
ALTER TABLE vdc ADD COLUMN allowstudycomments bool default true;
ALTER TABLE vdc ALTER COLUMN allowstudycomments SET STORAGE PLAIN;

ALTER TABLE studyfile ADD COLUMN fileclass character varying(255);
ALTER TABLE studyfile ALTER COLUMN fileclass SET STORAGE EXTENDED;

ALTER TABLE studyfile ADD COLUMN study_id bigint;
ALTER TABLE studyfile ALTER COLUMN study_id SET STORAGE PLAIN;

ALTER TABLE studyfile ADD COLUMN unf character varying(255);
ALTER TABLE studyfile ALTER COLUMN unf SET STORAGE EXTENDED;

ALTER TABLE datatable ADD COLUMN "type" character varying(255);
ALTER TABLE datatable ALTER COLUMN "type" SET STORAGE EXTENDED;

ALTER TABLE studyotherid ALTER COLUMN otherid type text;
--
-- and now DML
--
begin;

update dvnversion set buildnumber=0, versionnumber=2;

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditStudyCommentsPage', '/admin/EditStudyCommentsPage.xhtml', 3,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'CommentReviewPage', '/networkAdmin/CommentReviewPage.xhtml', null, 2 );

update studyfile set fileclass='TabularDataFile' where subsettable = true;
update studyfile set fileclass='OtherFile' where subsettable = false;

update studyfile sf set unf = dt.unf from datatable dt where sf.id = dt.studyfile_id;
update studyfile sf set study_id = fc.study_id from filecategory fc where sf.filecategory_id = fc.id;

-- Remove erroneous templates
delete from templatefield where template_id in(select id from template where vdc_id is null and id != 1);
delete from template  where vdc_id is null and id != 1;

-- Fix authorization for My Dataverses page
update pagedef set role_id = null, networkrole_id = null where name = 'MyDataversePage';
commit;
