
-- changes for default header and footer
update vdcnetwork set defaultvdcheader='<style type="text/css">
body {margin:0; padding:0;}
</style>
<div style="width:100%; height:40px; background: url(/dvn/resources/images/customizationpattern.png) repeat-x left -35px #698DA2;"></div>
<div style="margin:0 auto; max-width:1000px;">'

update vdcnetwork set defaultvdcfooter='</div>'

-- rrmoval of default description (replaced by new logic)
update vdcnetwork set defaultvdcannouncements = '';

update vdc  set announcements = ''
where announcements like 'A description of your Dataverse or announcements may be added here.%'


-- Changes for new template logic
Alter table studyField add dcmField boolean;

Alter table studyField add fieldType character varying(255);

update studyField set dcmField = false;

update studyField set dcmField = true
    where id > 43 and id < 65;

UPDATE studyfield
   SET  "name"='Kind of Data'
 WHERE id=44;

 UPDATE studyfield
   SET  "name"='Time Method'
 WHERE id=45;

  UPDATE studyfield
   SET  "name"='Data Collector'
 WHERE id=46;

UPDATE studyfield
   SET  "name"='Frequency of Data Collection'
 WHERE id=47;

UPDATE studyfield
   SET  "name"='Sampling Procedure'
 WHERE id=48;

UPDATE studyfield
   SET  "name"='Deviations from Sample Design'
 WHERE id=49;

UPDATE studyfield
   SET  "name"='Collection Mode'
 WHERE id=50;

 UPDATE studyfield
   SET  "name"='Research Instrument'
 WHERE id=51;

UPDATE studyfield
   SET  "name"='Data Sources'
 WHERE id=52;

 UPDATE studyfield
   SET  "name"='Origin of Sources'
 WHERE id=53;

UPDATE studyfield
   SET  "name"='Characteristic of Sources'
 WHERE id=54;

UPDATE studyfield
   SET  "name"='Access to Sources'
 WHERE id=55;

UPDATE studyfield
   SET  "name"='Data Collection Situation'
 WHERE id=56;

UPDATE studyfield
   SET  "name"='Actions to Minimize Loss'
 WHERE id=57;

UPDATE studyfield
   SET  "name"='Control Operations'
 WHERE id=58;

UPDATE studyfield
   SET  "name"='Weighting'
 WHERE id=59;

UPDATE studyfield
   SET  "name"='Cleaning Operations'
 WHERE id=60;

UPDATE studyfield
   SET  "name"='Study Level Error Notes'
 WHERE id=61;

UPDATE studyfield
   SET  "name"='Response Rate'
 WHERE id=62;

UPDATE studyfield
   SET  "name"='Sampling Error Estimates'
 WHERE id=63;

UPDATE studyfield
   SET  "name"='Other Data Appraisal'
 WHERE id=64;

ALTER TABLE templateField ADD COLUMN dcmSortOrder bigint;

alter table template
 ADD status  character varying(255);

 alter table templateField
 ADD dcmSortOrder  int;

update templatefield
set dcmsortorder = studyfield_id - 43
where studyfield_id > 43 and studyfield_id < 65;


 alter table templateField
 add ALLOWMULTIPLES boolean;

 update templateField
 set ALLOWMULTIPLES = false;



 Alter table studyAuthor add IDType  character varying(255);
 Alter table studyAuthor add IDValue  character varying(255);

Alter table template add network boolean;

UPDATE template
   SET  network=false
 WHERE vdc_id is not null;

 UPDATE template
   SET  network=true
 WHERE vdc_id is null;