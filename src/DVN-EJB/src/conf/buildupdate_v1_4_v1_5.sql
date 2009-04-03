
--
-- first do DDL (this cannot be rollbacked)
--

--- NONE SO FAR

--
-- and now DML
--
begin;


update dvnversion set buildnumber=5;

-- remove date of deposit from templates
update metadata set dateofdeposit = '' where id in (select metadata_id from template);

commit;
