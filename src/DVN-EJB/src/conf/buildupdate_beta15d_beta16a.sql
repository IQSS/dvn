ALTER TABLE vdcnetwork ADD COLUMN exporthourofday int4;
ALTER TABLE vdcnetwork ALTER COLUMN exporthourofday SET STORAGE PLAIN;

ALTER TABLE vdcnetwork ADD COLUMN exportdayofweek int4;
ALTER TABLE vdcnetwork ALTER COLUMN exportdayofweek SET STORAGE PLAIN;

ALTER TABLE vdcnetwork ADD COLUMN exportperiod varchar(255);
ALTER TABLE vdcnetwork ALTER COLUMN exportperiod SET STORAGE EXTENDED;

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditExportSchedulePage', '/networkAdmin/EditExportSchedulePage.jsp', null,2 );

ALTER TABLE study ADD COLUMN lastexporttime timestamp;
ALTER TABLE study ALTER COLUMN lastexporttime SET STORAGE PLAIN;

-- Column: dtype
-- ALTER TABLE vdc DROP COLUMN dtype;
ALTER TABLE vdc ADD COLUMN dtype varchar(31);
ALTER TABLE vdc ALTER COLUMN dtype SET STORAGE EXTENDED;

-- Column: lastname
-- ALTER TABLE vdc DROP COLUMN lastname;
ALTER TABLE vdc ADD COLUMN lastname varchar(255);
ALTER TABLE vdc ALTER COLUMN lastname SET STORAGE EXTENDED;

-- Column: firstname
-- ALTER TABLE vdc DROP COLUMN firstname;
ALTER TABLE vdc ADD COLUMN firstname varchar(255);
ALTER TABLE vdc ALTER COLUMN firstname SET STORAGE EXTENDED;

-- Column: affiliation
-- ALTER TABLE vdc DROP COLUMN affiliation;

ALTER TABLE vdc ADD COLUMN affiliation varchar(255);
ALTER TABLE vdc ALTER COLUMN affiliation SET STORAGE EXTENDED;

update VDC set dtype='Basic';


-- new queries needed to support harvestformattype

CREATE TABLE harvestformattype
(
  id int8 NOT NULL,
  metadataprefix varchar(255),
  name varchar(255),
  stylesheetfilename varchar(255),
  CONSTRAINT harvestformattype_pkey PRIMARY KEY (id)
) 
WITHOUT OIDS;
ALTER TABLE harvestformattype OWNER TO "vdcApp";

ALTER TABLE harvestingdataverse ADD COLUMN harvestformattype_id int8;
ALTER TABLE harvestingdataverse ALTER COLUMN harvestformattype_id SET STORAGE PLAIN;

ALTER TABLE harvestingdataverse
  ADD CONSTRAINT fk_harvestingdataverse_harvestformattype_id FOREIGN KEY (harvestformattype_id)
      REFERENCES harvestformattype (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

insert into harvestformattype values (0, 'ddi', 'DDI', null);
insert into harvestformattype values (1, 'oai_etdms', 'MIF', 'mif2ddi.xsl');

update harvestingdataverse hd
set harvestformattype_id = hft.id
from harvestformattype hft 
where hft.metadataprefix = hd.format;

-- to support originalFileType

ALTER TABLE studyfile ADD COLUMN originalfiletype varchar(255);
ALTER TABLE studyfile ALTER COLUMN originalfiletype SET STORAGE EXTENDED;