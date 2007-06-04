
--
-- TOC entry 1805 (class 0 OID 113780)
-- Dependencies: 1261
-- Data for Name: vdc; Type: TABLE DATA; Schema: public; Owner: vdcApp
--

ALTER TABLE vdc DISABLE TRIGGER ALL;

INSERT INTO vdc (id, version, description, copyright, header, logo, announcements, contactemail, footer, aboutthisdataverse, name, visibility, defaulttemplate_id, rootcollection_id, reviewstate_id, reviewer_id, restricted,allowcontributorrequests,displayAnnouncements,displayNetworkAnnouncements, displayNewStudies, alias,creator_id, termsOfUseEnabled, termsOfUse) VALUES (1, 1, 'VDC 1 Description', 'VDC 1 Copyright', '<table width="100%" cellpadding="0" cellspacing="0" title="banner" bgcolor="#EEEEEE" border="2"><tr><td><img src="/dvn/resources/custombanner.jpg"/></td></tr></table>', NULL, 'There are no local announcements.', 'admin@vdc1.com', '<table width="100%"><tr><td>Your footer text.</td></tr></table>', 'A description about this Dataverse network has not been entered yet.', 'VDC 1', NULL, 1, 1, NULL, NULL, FALSE,TRUE, TRUE,TRUE,TRUE,'gobu',1,true,'These are the Terms of Use.');

ALTER TABLE vdc ENABLE TRIGGER ALL;

ALTER TABLE vdcuser DISABLE TRIGGER ALL;

insert into vdcuser(id, version, firstname, lastname, username,  password, email,active ) VALUES ( 2, 1, 'Admin', 'Admin', 'admin','admin', 'vdc-dev@hmdc.harvard.edu',true);
insert into vdcuser(id, version,  firstname, lastname, username,  password,email,active  ) VALUES ( 3, 1, 'Contributor','Contributor', 'contributor' ,'contributor','vdc-dev@hmdc.harvard.edu',true);
insert into vdcuser(id, version,  firstname, lastname, username,  password,email,active  ) VALUES ( 4, 1, 'Curator','Curator', 'curator' ,'curator','vdc-dev@hmdc.harvard.edu',true);
insert into vdcuser(id, version,  firstname, lastname, username,  password,email,active  ) VALUES ( 5, 1, 'Viewer','Viewer', 'privilegedViewer' ,'privilegedViewer','vdc-dev@hmdc.harvard.edu',true);

ALTER TABLE vdcuser ENABLE TRIGGER ALL;


--
-- TOC entry 1819 (class 0 OID 113867)
-- Dependencies: 1282
-- Data for Name: vdcrole; Type: TABLE DATA; Schema: public; Owner: vdcApp
--

ALTER TABLE vdcrole DISABLE TRIGGER ALL;

insert into vdcrole( vdc_id, vdcuser_id,role_id) VALUES( 1, 2, 3);
insert into vdcrole(vdc_id, vdcuser_id,role_id) VALUES( 1, 3, 1);
insert into vdcrole(vdc_id, vdcuser_id,role_id) VALUES( 1, 4, 2);
insert into vdcrole(vdc_id, vdcuser_id,role_id) VALUES( 1, 5, 4);

ALTER TABLE vdcrole ENABLE TRIGGER ALL;

--
-- TOC entry 1857 (class 0 OID 0)
-- Dependencies: 1260
-- Name: vdc_id_seq; Type: SEQUENCE SET; Schema: public; Owner: vdcApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('vdc', 'id'), 2, true);

--
-- TOC entry 1847 (class 0 OID 0)
-- Dependencies: 1308
-- Name: study_id_seq; Type: SEQUENCE SET; Schema: public; Owner: vdcApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('study', 'id'), 20, true);


--
-- TOC entry 1858 (class 0 OID 0)
-- Dependencies: 1252
-- Name: vdccollection_id_seq; Type: SEQUENCE SET; Schema: public; Owner: vdcApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('vdccollection', 'id'), 6, true);

--
-- TOC entry 1852 (class 0 OID 0)
-- Dependencies: 1267
-- Name: template_id_seq; Type: SEQUENCE SET; Schema: public; Owner: vdcApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('"template"', 'id'), 1, false);

--
-- TOC entry 1853 (class 0 OID 0)
-- Dependencies: 1264
-- Name: templatefield_id_seq; Type: SEQUENCE SET; Schema: public; Owner: vdcApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('templatefield', 'id'), 10, false);


--
-- TOC entry 1854 (class 0 OID 0)
-- Dependencies: 1277
-- Name: templatefieldselectvalue_id_seq; Type: SEQUENCE SET; Schema: public; Owner: vdcApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('templatefieldselectvalue', 'id'), 10, false);



--
-- TOC entry 1799 (class 0 OID 113744)
-- Dependencies: 1253
-- Data for Name: vdccollection; Type: TABLE DATA; Schema: public; Owner: vdcApp
--

ALTER TABLE vdccollection DISABLE TRIGGER ALL;

INSERT INTO vdccollection (id, version, name, displayorder, longdesc, query, shortdesc, owner_id,parentcollection_id,visible) VALUES (1, 1, 'VDC 1 Root Collection', NULL, NULL, NULL, NULL, 1, NULL,true);
INSERT INTO vdccollection (id, version, name, displayorder, longdesc, query, shortdesc, owner_id,parentcollection_id,visible) VALUES (2, 1, 'Top Level Collection', NULL, NULL, NULL, NULL, 1,1,true);
INSERT INTO vdccollection (id, version, name, displayorder, longdesc, query, shortdesc, owner_id,parentcollection_id,visible) VALUES (3, 1, 'Another Top Level Collection', NULL, NULL, NULL, NULL, 1,1,true);
INSERT INTO vdccollection (id, version, name, displayorder, longdesc, query, shortdesc, owner_id,parentcollection_id,visible) VALUES (4, 1, 'Subcollection A', NULL, NULL, NULL, NULL, 1,2,true);
INSERT INTO vdccollection (id, version, name, displayorder, longdesc, query, shortdesc, owner_id,parentcollection_id,visible) VALUES (5, 1, 'Subcollection B', NULL, NULL, NULL, NULL, 1,2,true);

ALTER TABLE vdccollection ENABLE TRIGGER ALL;


--
-- TOC entry 1822 (class 0 OID 113882)
-- Dependencies: 1286
-- Data for Name: coll_studies; Type: TABLE DATA; Schema: public; Owner: vdcApp
--

ALTER TABLE coll_studies DISABLE TRIGGER ALL;

INSERT INTO coll_studies (vdc_collection_id, study_id) VALUES (1, 6);
INSERT INTO coll_studies (vdc_collection_id, study_id) VALUES (2, 1);
INSERT INTO coll_studies (vdc_collection_id, study_id) VALUES (2, 2);
INSERT INTO coll_studies (vdc_collection_id, study_id) VALUES (2, 4);
INSERT INTO coll_studies (vdc_collection_id, study_id) VALUES (3, 2);
INSERT INTO coll_studies (vdc_collection_id, study_id) VALUES (3, 3);
INSERT INTO coll_studies (vdc_collection_id, study_id) VALUES (4, 3);
INSERT INTO coll_studies (vdc_collection_id, study_id) VALUES (4, 4);
INSERT INTO coll_studies (vdc_collection_id, study_id) VALUES (5, 4);
INSERT INTO coll_studies (vdc_collection_id, study_id) VALUES (5, 5);





ALTER TABLE coll_studies ENABLE TRIGGER ALL;

--
-- TOC entry 1833 (class 0 OID 113955)
-- Dependencies: 1305
-- Data for Name: datatable; Type: TABLE DATA; Schema: public; Owner: vdcApp
--

ALTER TABLE datatable DISABLE TRIGGER ALL;


--
-- TOC entry 1836 (class 0 OID 113970)
-- Dependencies: 1309
-- Data for Name: study; Type: TABLE DATA; Schema: public; Owner: vdcApp
--

ALTER TABLE study DISABLE TRIGGER ALL;

INSERT INTO study (id, title, isHarvested, version, numberofdownloads, template_id,restricted,requestAccess, owner_id, creator_id,reviewstate_id) VALUES (1,'Test Study A', FALSE, 1, 11,1,FALSE,FALSE,1,4,3);
INSERT INTO study (id, title, isHarvested, version, numberofdownloads, template_id,restricted,requestAccess, owner_id, creator_id,reviewstate_id) VALUES (2,'Test Study B',FALSE, 1, 9,1,FALSE,FALSE,1,4,3);
INSERT INTO study (id, title, isHarvested, version, numberofdownloads, template_id,restricted,requestAccess, owner_id, creator_id,reviewstate_id) VALUES (3,'Test Study C',FALSE, 1, 35,1,FALSE,FALSE,1,4,3);
INSERT INTO study (id, title, isHarvested, version, numberofdownloads, template_id,restricted,requestAccess, owner_id, creator_id,reviewstate_id) VALUES (4,'Test Study D', FALSE, 1, 23,1,FALSE,FALSE,1,4,3);
INSERT INTO study (id, title, isHarvested, version, numberofdownloads, template_id,restricted,requestAccess, owner_id, creator_id,reviewstate_id) VALUES (5,'Test Study E',FALSE, 1, 27,1,FALSE,FALSE,1,4,3);
INSERT INTO study (id, title, isHarvested, version, numberofdownloads, template_id,restricted,requestAccess, owner_id, creator_id,reviewstate_id) VALUES (6,'Test Study F',FALSE, 1, 2,1,FALSE,FALSE,1,4,3);

ALTER TABLE study ENABLE TRIGGER ALL;



