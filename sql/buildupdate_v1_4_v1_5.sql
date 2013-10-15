
--
-- first do DDL (this cannot be rollbacked)
--

--- NONE SO FAR

--
-- and now DML
--
begin;


update dvnversion set buildnumber=5;


INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ContributorRequestInfoPage', '/login/ContributorRequestInfoPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'CreatorRequestInfoPage','/login/CreatorRequestInfoPage.xhtml', null,null );
update pagedef set networkrole_id = 2 where name = 'EditHarvestSitePage';
update pagedef set networkrole_id = 2 where name =  'NetworkOptionsPage';
update pagedef set networkrole_id = 2 where name =  'HarvestSitesPage';
update pagedef set networkrole_id = 2 where name =  'AddClassificationsPage';
update pagedef set networkrole_id = 2 where name =  'ManageClassificationsPage';
update pagedef set networkrole_id = 2 where name =  'ManageDataversesPage';

DELETE FROM pagedef where name = 'EditNetworkNamePage';
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditNetworkNamePage', '/networkAdmin/EditNetworkNamePage.xhtml', null,2 );

-- remove date of deposit from templates
update metadata set dateofdeposit = '' where id in (select metadata_id from template);

commit;
