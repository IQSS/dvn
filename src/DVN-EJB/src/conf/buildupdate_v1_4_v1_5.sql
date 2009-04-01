
--
-- first do DDL (this cannot be rollbacked)
--

--- NONE SO FAR

--
-- and now DML
--
begin;


update dvnversion set buildnumber=5;


commit;
