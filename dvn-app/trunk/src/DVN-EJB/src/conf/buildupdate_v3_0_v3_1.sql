ALTER TABLE template ADD description character varying(255);

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditGuestbookQuestionnairePage', '/admin/EditGuestbookQuestionnairePage.xhtml', 3,null );