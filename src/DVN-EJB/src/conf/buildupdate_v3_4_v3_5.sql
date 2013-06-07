update vdcnetwork set id = 0 where id = 1;
select setval('vdcnetwork_id_seq',1,false);

Alter TABLE vdc add vdcnetwork_id bigint;

Update vdc set vdcnetwork_id = 0;

ALTER TABLE vdcnetwork ADD COLUMN urlalias character varying(255);
ALTER TABLE vdcnetwork ALTER COLUMN urlalias SET STORAGE EXTENDED;

ALTER TABLE vdcnetwork ADD COLUMN networkcreated timestamp;
ALTER TABLE vdcnetwork ALTER COLUMN networkcreated SET STORAGE PLAIN;

ALTER TABLE vdcnetwork ADD COLUMN affiliation character varying(255);
ALTER TABLE vdcnetwork ALTER COLUMN affiliation SET STORAGE EXTENDED;

ALTER TABLE vdcnetwork ADD COLUMN creator_id bigint;
ALTER TABLE vdcnetwork ALTER COLUMN creator_id SET STORAGE PLAIN;

ALTER TABLE vdcnetwork ADD COLUMN released boolean;

ALTER TABLE vdcnetwork ADD COLUMN logo character varying(255);
ALTER TABLE vdcnetwork ADD COLUMN shortdescription character varying(255);

Alter TABLE template add vdcsubnetwork_id bigint;

Update template set vdcsubnetwork_id = 0;

Alter TABLE vdcnetworkstats add vdcnetwork_id bigint;
update vdcnetworkstats set vdcnetwork_id = 0;

update template set displayoncreatedataverse = true
where vdc_id is null;

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditSubnetworkPage', '/networkAdmin/EditSubnetworkPage.xhtml', null, 2 );

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManageSubnetworksPage', '/networkAdmin/ManageSubnetworksPage.xhtml', null, 2 );

-- Data changes for advanced search
update vdc_adv_search_fields set study_field_id = 99 where study_field_id = 3; -- author to authorName
update vdc_adv_search_fields set study_field_id = 100 where study_field_id = 5; -- producer to producerName
update vdc_adv_search_fields set study_field_id = 101 where study_field_id = 15; -- distributor to distributorName
update vdc_adv_search_fields set study_field_id = 104 where study_field_id = 27; -- keyword to keywordValue
update vdc_adv_search_fields set study_field_id = 105 where study_field_id = 30; -- topicClassification to topicClassValue
update vdc_adv_search_fields set study_field_id = 113 where study_field_id = 79; -- publication to publicationCitation

insert into vdc_adv_search_fields (vdc_id, study_field_id)  select distinct vdc_id, 89 from vdc_adv_search_fields where vdc_id not in 
(select vdc_id from vdc_adv_search_fields where study_field_id=89) -- add publication ReplicationFor
