ALTER TABLE deletedstudy DROP COLUMN authority;
ALTER TABLE deletedstudy DROP COLUMN studyid;
ALTER TABLE deletedstudy ADD COLUMN globalid varchar(255);

ALTER TABLE deletedstudy ADD COLUMN deletedtime timestamp;
ALTER TABLE deletedstudy ALTER COLUMN deletedtime SET STORAGE PLAIN;
