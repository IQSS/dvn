
Alter Table vdcnetwork
add requireDVDescription boolean,
add requireDVaffiliation boolean,
add requireDVclassification boolean,
add requireDVstudiesforrelease boolean;

update vdcnetwork set  requireDVDescription = false,
 requireDVaffiliation = false,
 requireDVclassification = false,
 requireDVstudiesforrelease = false;

ALTER TABLE oaiset ADD COLUMN lockssconfig_id bigint;
ALTER TABLE oaiset ALTER COLUMN lockssconfig_id SET STORAGE PLAIN;
