begin;

UPDATE pagedef SET path=regexp_replace(pagedef.path, '.jsp', '.xhtml', 'g');

-- Column: releasedate

-- ALTER TABLE vdc DROP COLUMN releasedate;

ALTER TABLE vdc ADD COLUMN releasedate timestamp;
ALTER TABLE vdc ALTER COLUMN releasedate SET STORAGE PLAIN;

-- Column: createddate

-- ALTER TABLE vdc DROP COLUMN createddate;

ALTER TABLE vdc ADD COLUMN createddate timestamp;
ALTER TABLE vdc ALTER COLUMN createddate SET STORAGE PLAIN;


-- Column: dvndescription

-- ALTER TABLE vdc DROP COLUMN dvndescription;

ALTER TABLE vdc ADD COLUMN dvndescription varchar(255);
ALTER TABLE vdc ALTER COLUMN dvndescription SET STORAGE EXTENDED;

ALTER TABLE harvestingdataverse RENAME COLUMN oaiserver TO serverurl;

drop table deletedstudy;

alter table oaiset
alter column definition type text;

-- Column: parent

-- ALTER TABLE vdcgroup DROP COLUMN parent;

ALTER TABLE vdcgroup ADD COLUMN parent int4;
ALTER TABLE vdcgroup ALTER COLUMN parent SET STORAGE PLAIN;


ALTER TABLE harvestingdataverse ADD COLUMN lastsuccessfulharvesttime timestamp;
ALTER TABLE harvestingdataverse ALTER COLUMN lastsuccessfulharvesttime SET STORAGE PLAIN;


ALTER TABLE harvestingdataverse ADD COLUMN harvestedstudycount int8;
ALTER TABLE harvestingdataverse ALTER COLUMN harvestedstudycount SET STORAGE PLAIN;


ALTER TABLE harvestingdataverse ADD COLUMN failedstudycount int8;
ALTER TABLE harvestingdataverse ALTER COLUMN failedstudycount SET STORAGE PLAIN;


ALTER TABLE harvestingdataverse ADD COLUMN harvestresult varchar(255);
ALTER TABLE harvestingdataverse ALTER COLUMN harvestresult SET STORAGE EXTENDED;

update harvestingdataverse set lastsuccessfulharvesttime=lastharvesttime;



-- Changes to vdccollection table

alter table vdccollection drop column visible;

alter table vdccollection drop column reviewstate_id;

alter table vdccollection drop column shortdesc;

alter table vdccollection rename column longdesc to description;

ALTER TABLE vdccollection ADD COLUMN localscope boolean;
ALTER TABLE vdccollection ALTER COLUMN localscope SET STORAGE PLAIN;

ALTER TABLE vdccollection ADD COLUMN type varchar(255);
ALTER TABLE vdccollection ALTER COLUMN type SET STORAGE EXTENDED;

update vdccollection set localscope = false;
update vdccollection set type='static' where query is null;
update vdccollection set type='dynamic' where query is not null;

update dvnversion set buildnumber=4;


-- remove owned studies from root collection
delete from coll_studies
where study_id || '_' || vdc_collection_id in
(
	select study_id || '_' || rootcollection_id
	from study, vdc
	where study.owner_id = vdc.id
);


delete from pagedef where name = 'AddCollectionsPage';
update pagedef set name = 'ManageCollectionsPage' where name = 'ManageCollectionPage';
update pagedef set path = '/collection/ManageCollectionsPage.xhtml' where path = '/collection/ManageCollectionPage.xhtml';

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'AddClassificationsPage', '/networkAdmin/AddClassificationsPage.xhtml', null,1 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManageClassificationsPage', '/networkAdmin/ManageClassificationsPage.xhtml', null,1 );


commit;
