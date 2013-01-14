ALTER TABLE deletedstudy ADD COLUMN globalid varchar(255);
ALTER TABLE deletedstudy ALTER COLUMN globalid SET STORAGE EXTENDED;

ALTER TABLE deletedstudy ADD COLUMN deletedtime timestamp;
ALTER TABLE deletedstudy ALTER COLUMN deletedtime SET STORAGE PLAIN;

update deletedstudy set globalid = 'hdl:' || authority  || '/' || studyId;
update deletedstudy set deletedtime = current_timestamp(3);

ALTER TABLE deletedstudy DROP COLUMN authority;
ALTER TABLE deletedstudy DROP COLUMN studyid;


ALTER TABLE studykeyword ALTER COLUMN value type text;
ALTER TABLE studykeyword ALTER COLUMN value SET STORAGE EXTENDED;

