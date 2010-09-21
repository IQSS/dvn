
ALTER TABLE study DROP COLUMN defaultfilecategory_id;
DROP TABLE filecategory;

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

insert into licensetype (id, shortname, name, licenseurl, rdfurl, imageurl) values ( 1, 'cc by', 'CC Attribution (cc by)', 'http://creativecommons.org/licenses/by/3.0/', 'http://creativecommons.org/licenses/by/3.0/rdf', 'http://i.creativecommons.org/l/by/3.0/88x31.png' );
insert into licensetype (id, shortname, name, licenseurl, rdfurl, imageurl) values ( 2, 'cc0','CC Zero (cc0)','http://creativecommons.org/publicdomain/zero/1.0/','http://creativecommons.org/publicdomain/zero/1.0/rdf','http://i.creativecommons.org/l/zero/1.0/88x31.png');
insert into licensetype (id, shortname, name, licenseurl, rdfurl, imageurl) values ( 3, 'cc by-sa','CC Attribution Share Alike (cc by-sa)','http://creativecommons.org/licenses/by-sa/3.0/', 'http://creativecommons.org/licenses/by-sa/3.0/rdf', 'http://i.creativecommons.org/l/by-sa/3.0/88x31.png' );
insert into licensetype (id, shortname, name, licenseurl, rdfurl, imageurl) values ( 4, 'cc by-nd','CC Attribution No Derivatives (cc by-nd)','http://creativecommons.org/licenses/by-nd/3.0/', 'http://creativecommons.org/licenses/by-nd/3.0/rdf', 'http://i.creativecommons.org/l/by-nd/3.0/88x31.png' );
insert into licensetype (id, shortname, name, licenseurl, rdfurl, imageurl) values ( 5, 'cc by-nc','CC Attribution Non-Commercial (cc by-nc)','http://creativecommons.org/licenses/by-nc/3.0/', 'http://creativecommons.org/licenses/by-nc/3.0/rdf', 'http://i.creativecommons.org/l/by-nc/3.0/88x31.png' );
insert into licensetype (id, shortname, name, licenseurl, rdfurl, imageurl) values ( 6, 'cc by-nc-sa','CC Attribution Non-Commercial Share Alike (cc by-nc-sa)','http://creativecommons.org/licenses/by-nc-sa/3.0/', 'http://creativecommons.org/licenses/by-nc-sa/3.0/rdf', 'http://i.creativecommons.org/l/by-nc-sa/3.0/88x31.png' );
insert into licensetype (id, shortname, name, licenseurl, rdfurl, imageurl) values ( 7, 'cc by-nc-nd','CC Attribution Non-Commercial No Derivatives (cc by-nc-nd)','http://creativecommons.org/licenses/by-nc-sa/3.0/', 'http://creativecommons.org/licenses/by-nc-sa/3.0/rdf', 'http://i.creativecommons.org/l/by-nc-sa/3.0/88x31.png' );


ALTER TABLE vdc ADD COLUMN displayinframe boolean;
ALTER TABLE vdc ALTER COLUMN displayinframe SET STORAGE PLAIN;

ALTER TABLE vdc ADD COLUMN parentsite character varying(255);
ALTER TABLE vdc ALTER COLUMN parentsite SET STORAGE EXTENDED;

update vdc set displayinframe = false;

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditLockssConfigPage','/admin/EditLockssConfigPage.xhtml',3,2 );