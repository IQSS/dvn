
--
-- first do DDL (this cannot be rollbacked)
--

--- NONE SO FAR

--
-- and now DML
--
begin;


update dvnversion set buildnumber=0, versionnumber=2;


commit;
