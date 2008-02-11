ALTER TABLE vdcnetwork ADD COLUMN termsOfUse text;
ALTER TABLE vdcnetwork ALTER COLUMN termsOfUse SET STORAGE EXTENDED;

ALTER TABLE vdcnetwork ADD COLUMN depositTermsOfUse text;
ALTER TABLE vdcnetwork ALTER COLUMN depositTermsOfUse SET STORAGE EXTENDED;

ALTER TABLE vdcnetwork ADD COLUMN deposittermsofuseenabled bool;
ALTER TABLE vdcnetwork ALTER COLUMN deposittermsofuseenabled SET STORAGE PLAIN;

ALTER TABLE vdcnetwork ADD COLUMN downloadTermsOfUse text;
ALTER TABLE vdcnetwork ALTER COLUMN downloadTermsOfUse SET STORAGE EXTENDED;

ALTER TABLE vdcnetwork ADD COLUMN downloadtermsofuseenabled bool;
ALTER TABLE vdcnetwork ALTER COLUMN downloadtermsofuseenabled SET STORAGE PLAIN;

ALTER TABLE vdcnetwork ADD COLUMN termsofuseupdated timestamp;
ALTER TABLE vdcnetwork ALTER COLUMN termsofuseupdated SET STORAGE PLAIN;

ALTER TABLE vdcnetwork ADD COLUMN defaultdisplaynumber int4;
ALTER TABLE vdcnetwork ALTER COLUMN defaultdisplaynumber SET STORAGE PLAIN;

ALTER TABLE vdc ADD COLUMN deposittermsofuseenabled bool;
ALTER TABLE vdc ALTER COLUMN deposittermsofuseenabled SET STORAGE PLAIN;

ALTER TABLE vdcgroup ADD COLUMN defaultdisplaynumber int4;
ALTER TABLE vdcgroup ALTER COLUMN defaultdisplaynumber SET STORAGE PLAIN;

ALTER TABLE vdcuser ADD COLUMN agreedtermsofuse bool;
ALTER TABLE vdcuser ALTER COLUMN agreedtermsofuse SET STORAGE PLAIN;

update vdc set deposittermsofuseenabled=false;
update vdcgroup set defaultDisplayNumber=16;
update vdcnetwork set deposittermsofuseenabled=false;
update vdcnetwork set downloadtermsofuseenabled=false;
update vdcnetwork set defaultdisplaynumber=16;
update vdcuser set agreedtermsofuse=false;

update dvnversion set versionnumber=1, buildnumber=1;