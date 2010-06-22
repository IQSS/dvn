begin;

--
-- first do DDL
--

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

alter table oaiset alter column definition type text;

alter table oaiset alter column name type text;
alter table oaiset alter column spec type text;
alter table oaiset alter column description type text;

ALTER TABLE variablecategory ALTER COLUMN value type text;

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

-- new studyId column (and fk constraint) to studyFileActivity
ALTER TABLE studyfileactivity ADD COLUMN study_id bigint;
ALTER TABLE studyfileactivity ALTER COLUMN study_id SET STORAGE PLAIN;

ALTER TABLE studyfileactivity
  ADD CONSTRAINT fk_studyfileactivity_study_id FOREIGN KEY (study_id)
      REFERENCES study (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- Changes to vdccollection table
alter table vdccollection drop column visible;

alter table vdccollection drop column reviewstate_id;

alter table vdccollection drop column shortdesc;

alter table vdccollection rename column longdesc to description;
ALTER TABLE vdccollection ALTER COLUMN description type text;
ALTER TABLE vdccollection ALTER COLUMN description SET STORAGE EXTENDED;

ALTER TABLE vdccollection ADD COLUMN localscope boolean;
ALTER TABLE vdccollection ALTER COLUMN localscope SET STORAGE PLAIN;

ALTER TABLE vdccollection ADD COLUMN type varchar(255);
ALTER TABLE vdccollection ALTER COLUMN type SET STORAGE EXTENDED;


-- new indices
create index study_owner_id_index on study(owner_id);
create index filecategory_id_index on filecategory(id);
create index studyfile_id_index on studyfile(id);
create index studyfileactivity_id_index on studyfileactivity(id);
create index studyfileactivity_studyfile_id_index on studyfileactivity(studyfile_id);
create index studyfileactivity_study_id_index on studyfileactivity(study_id);

--
-- and now DML
--

update dvnversion set buildnumber=4;

update harvestingdataverse set lastsuccessfulharvesttime=lastharvesttime;

-- vdccollection chabges
update vdccollection set localscope = false;
update vdccollection set type='static' where query is null;
update vdccollection set type='dynamic' where query is not null;


-- remove owned studies from root collection
delete from coll_studies
where study_id || '_' || vdc_collection_id in
(
	select study_id || '_' || rootcollection_id
	from study, vdc
	where study.owner_id = vdc.id
);


-- pagedef changes
UPDATE pagedef SET path=regexp_replace(pagedef.path, '.jsp', '.xhtml', 'g');
delete from pagedef where name = 'AddCollectionsPage';
update pagedef set name = 'ManageCollectionsPage' where name = 'ManageCollectionPage';
update pagedef set path = '/collection/ManageCollectionsPage.xhtml' where path = '/collection/ManageCollectionPage.xhtml';

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'AddClassificationsPage', '/networkAdmin/AddClassificationsPage.xhtml', null,1 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManageClassificationsPage', '/networkAdmin/ManageClassificationsPage.xhtml', null,1 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManageDataversesPage', '/networkAdmin/ManageDataversesPage.xhtml', null,1 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'MyDataversePage','/networkAdmin/MyDataversePage.xhtml',1,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManageStudiesPage','/study/ManageStudiesPage.xhtml',1,null );


-- remove production date from default seatch results (nad current vdcs)
update studyfield set searchresultfield = false where name = 'productionDate';

delete from search_result_fields
where study_field_id = (select id from studyfield where name = 'productionDate');



-- populate the new column
update studyfileactivity set study_id = fc.study_id
from studyfile sf, filecategory fc
where studyfileactivity.studyfile_id = sf.id
and sf.filecategory_id = fc.id;


-- fix the templatefield data
delete from templatefield  where template_id is null;

update templatefield set version = 1 where template_id = 1;

insert into templatefield (id, version, template_id, studyfield_id, fieldinputlevel_id)
SELECT nextval('templatefield_id_seq'), 1, t.id, tf.studyfield_id, tf.fieldinputlevel_id
from template t, templatefield tf
where t.id != 1
and tf.template_id = 1;


-- update createddate and releasedate on current vdcs
update vdc
set createddate = s.createtime
from (
select owner_id, min(createtime) as createtime
from study
group by study.owner_id
) s
where vdc.id = s.owner_id
and vdc.createddate is null;

update vdc
set createddate = current_date
where vdc.createddate is null;

update vdc
set releasedate = createddate
where vdc.releasedate is null
and restricted = false;

-- populate new vdcnetworkstats table
INSERT INTO vdcnetworkstats (id,studycount,filecount) values (1,0,0);

-- populate  new vdcactivity table
insert into vdcactivity ( id, localstudylocaldownloadcount, localstudynetworkdownloadcount, localstudyforeigndownloadcount, foreignstudylocaldownloadcount, vdc_id )
SELECT nextval('vdcactivity_id_seq'), 0, 0, 0, 0, id
from vdc;

-- and studyfileactivity (for nonexisting files)
insert into studyfileactivity ( id, downloadcount, study_id, studyfile_id )
SELECT nextval('studyfileactivity_id_seq'), 0, fc.study_id, sf.id
from studyfile sf, filecategory fc
where fc.id = sf.filecategory_id
and sf.id not in (select studyfile_id from studyfileactivity);

update vdc set affiliation = '' where affiliation is null;

commit;
