--DB Changes for Beta_12b :

ALTER TABLE harvestingdataverse ADD COLUMN harvestingnow bool;
ALTER TABLE harvestingdataverse ALTER COLUMN harvestingnow SET STORAGE PLAIN;

update harvestingdataverse set harvestingnow = false;

-- ADD new entity HandlePrefix with relationship to HarvestingDataverse
CREATE TABLE handleprefix
(
  id serial NOT NULL,
  prefix varchar(255),
  CONSTRAINT handleprefix_pkey PRIMARY KEY (id)
) 
WITHOUT OIDS;
ALTER TABLE handleprefix OWNER TO "vdcApp";


ALTER TABLE harvestingdataverse ADD COLUMN handleprefix_id int8;
ALTER TABLE harvestingdataverse ALTER COLUMN handleprefix_id SET STORAGE PLAIN;

ALTER TABLE harvestingdataverse
  ADD CONSTRAINT fk_harvestingdataverse_handleprefix_id FOREIGN KEY (handleprefix_id)
      REFERENCES handleprefix (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
