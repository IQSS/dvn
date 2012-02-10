--
-- PostgreSQL database dump
--

-- Started on 2006-09-19 16:05:05 Eastern Standard Time

SET client_encoding = 'UTF8';
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('metadata', 'id'), 10, false);

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('template', 'id'), 10, false);


--
-- TOC entry 1840 (class 0 OID 0)
-- Dependencies: 1304
-- Name: datatable_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dvnApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('pagedef', 'id'), 500, false);


SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('datatable', 'id'), 1, false);


--
-- TOC entry 1841 (class 0 OID 0)
-- Dependencies: 1291
-- Name: datavariable_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dvnApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('datavariable', 'id'), 1, false);


--
-- TOC entry 1842 (class 0 OID 0)
-- Dependencies: 1297
-- Name: fieldinputlevel_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dvnApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('fieldinputlevel', 'id'), 10, false);





--
-- TOC entry 1844 (class 0 OID 0)
-- Dependencies: 1287
-- Name: logindomain_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dvnApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('logindomain', 'id'), 1, false);




--
-- TOC entry 1846 (class 0 OID 0)
-- Dependencies: 1312
-- Name: role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dvnApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('"role"', 'id'), 10, false);


SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('"networkrole"', 'id'), 10, false);

--
-- TOC entry 1848 (class 0 OID 0)
-- Dependencies: 1272
-- Name: studyfield_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dvnApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('studyfield', 'id'), 150, true);





--
-- TOC entry 1851 (class 0 OID 0)
-- Dependencies: 1270
-- Name: studyfile_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dvnApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('studyfile', 'id'), 1, false);





--
-- TOC entry 1856 (class 0 OID 0)
-- Dependencies: 1302
-- Name: usergroup_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dvnApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('usergroup', 'id'), 1, false);



--
-- TOC entry 1859 (class 0 OID 0)
-- Dependencies: 1299
-- Name: vdcgroup_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dvnApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('vdcgroup', 'id'), 1, false);


--
-- TOC entry 1860 (class 0 OID 0)
-- Dependencies: 1289
-- Name: vdcnetwork_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dvnApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('vdcnetwork', 'id'), 1, true);


--
-- TOC entry 1861 (class 0 OID 0)
-- Dependencies: 1294
-- Name: vdcuser_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dvnApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('vdcuser', 'id'), 10, false);



--
-- TOC entry 1813 (class 0 OID 113837)
-- Dependencies: 1274
-- Data for Name: coll_adv_search_fields; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE coll_adv_search_fields DISABLE TRIGGER ALL;



ALTER TABLE coll_adv_search_fields ENABLE TRIGGER ALL;

--
-- TOC entry 1818 (class 0 OID 113863)
-- Dependencies: 1281
-- Data for Name: coll_any_search_fields; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE coll_any_search_fields DISABLE TRIGGER ALL;



ALTER TABLE coll_any_search_fields ENABLE TRIGGER ALL;

--
-- TOC entry 1804 (class 0 OID 113774)
-- Dependencies: 1259
-- Data for Name: coll_search_result_fields; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE coll_search_result_fields DISABLE TRIGGER ALL;



ALTER TABLE coll_search_result_fields ENABLE TRIGGER ALL;





ALTER TABLE datatable ENABLE TRIGGER ALL;

--
-- TOC entry 1825 (class 0 OID 113902)
-- Dependencies: 1292
-- Data for Name: datavariable; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE datavariable DISABLE TRIGGER ALL;



ALTER TABLE datavariable ENABLE TRIGGER ALL;

--
-- TOC entry 1829 (class 0 OID 113927)
-- Dependencies: 1298
-- Data for Name: fieldinputlevel; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE fieldinputlevel DISABLE TRIGGER ALL;

INSERT INTO fieldinputlevel (id, name ) VALUES (1, 'required');
INSERT INTO fieldinputlevel (id, name ) VALUES(2, 'recommended');
INSERT INTO fieldinputlevel (id, name ) VALUES(3, 'optional');



ALTER TABLE fieldinputlevel ENABLE TRIGGER ALL;



--
-- TOC entry 1823 (class 0 OID 113888)
-- Dependencies: 1288
-- Data for Name: logindomain; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE logindomain DISABLE TRIGGER ALL;



ALTER TABLE logindomain ENABLE TRIGGER ALL;



--
-- TOC entry 1838 (class 0 OID 113987)
-- Dependencies: 1313
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE "role" DISABLE TRIGGER ALL;
INSERT into role(id, name) VALUES (1, 'contributor');
INSERT into role(id, name) VALUES (2, 'curator');
INSERT into role(id, name) VALUES (3, 'admin');
INSERT into role(id,name) VALUES (4, 'privileged viewer');
ALTER TABLE "role" ENABLE TRIGGER ALL;

ALTER TABLE "networkrole" DISABLE TRIGGER ALL;
INSERT into networkrole(id, name) VALUES (1, 'Creator');
INSERT into networkrole(id, name) VALUES (2, 'Admin');
ALTER TABLE "networkrole" ENABLE TRIGGER ALL;

ALTER TABLE pagedef DISABLE TRIGGER ALL;


-- Pages that don't require role authorization
INSERT INTO pagedef (name, path, role_id, networkrole_id ) VALUES  ( 'StudyPage', '/study/StudyPage.xhtml', null,null );
INSERT INTO pagedef (name, path, role_id, networkrole_id ) VALUES  ( 'SubsettingPage', '/subsetting/SubsettingPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ExploreDataPage','/viz/ExploreDataPage.xhtml',null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ErrorPage', '/ErrorPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'HomePage', '/HomePage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'UnauthorizedPage', '/login/UnauthorizedPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'StudyLockedPage', '/login/StudyLockedPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'LogoutPage', '/login/LogoutPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'AddAccountPage', '/login/AddAccountPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditAccountPage', '/login/EditAccountPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'AccountOptionsPage', '/login/AccountOptionsPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'AccountPage', '/login/AccountPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'LoginPage', '/login/LoginPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ForgotPasswordPage', '/login/ForgotPasswordPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ContributorRequestPage', '/login/ContributorRequestPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ContributorRequestInfoPage', '/login/ContributorRequestInfoPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'CreatorRequestPage','/login/CreatorRequestPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'CreatorRequestInfoPage','/login/CreatorRequestInfoPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'TermsOfUsePage','/login/TermsOfUsePage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'AccountTermsOfUsePage','/login/AccountTermsOfUsePage.xhtml', null,null );
INSERT INTO pagedef (name, path, role_id, networkrole_id ) VALUES  ( 'StudyVersionDifferencesPage', '/study/StudyVersionDifferencesPage.xhtml', null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'OptionsPage','/admin/OptionsPage.xhtml',null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManageStudiesPage','/study/ManageStudiesPage.xhtml',null,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManifestPage', '/ManifestPage.xhtml', null,null );

-- Pages that require VDC Role authorization:
-- Contributor Role 
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditStudyPage','/study/EditStudyPage.xhtml',1,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditStudyFilesPage','/study/EditStudyFilesPage.xhtml',1,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'AddFilesPage','/study/AddFilesPage.xhtml',1,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'SetUpDataExplorationPage','/study/SetUpDataExplorationPage.xhtml',1,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'DeleteStudyPage','/study/DeleteStudyPage.xhtml',1,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditTemplatePage','/study/EditTemplatePage.xhtml',1,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'TemplateFormPage','/study/TemplateFormPage.xhtml',1,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'MyDataversePage','/networkAdmin/MyDataversePage.xhtml',null,null );

-- Curator Role
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditCollectionPage','/collection/EditCollectionPage.xhtml',2,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManageCollectionsPage','/collection/ManageCollectionsPage.xhtml',2,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'StudyPermissionsPage','/study/StudyPermissionsPage.xhtml',2,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'DeaccessionStudyPage', '/study/DeaccessionStudyPage.xhtml', 2, null );

-- Admin Role
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditSitePage', '/site/EditSitePage.xhtml', 3,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditBannerFooterPage','/admin/EditBannerFooterPage.xhtml',3,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditContactUsPage','/admin/EditContactUsPage.xhtml',3,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditHomePanelsPage','/admin/EditHomePanelsPage.xhtml',3,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditStudyCommentsPage', '/admin/EditStudyCommentsPage.xhtml', 3,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditUserTermsPage','/admin/EditUseTermsPage.xhtml',3,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditDepositUseTermsPage','/admin/EditDepositUseTermsPage.xhtml',3,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'PrivilegedUsersPage','/admin/PrivilegedUsersPage.xhtml',3,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'SearchFieldsPage','/admin/SearchFieldsPage.xhtml',3,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'PromotionalLinkSearchBoxPage','/admin/PromotionalLinkSearchBoxPage.xhtml',3,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditLockssConfigPage','/admin/EditLockssConfigPage.xhtml',3,2 );
-- Pages that require Network Role authorization
-- Creator Role 
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'AddSitePage', '/site/AddSitePage.xhtml', null,1 );
-- Admin Role
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'NetworkOptionsPage', '/networkAdmin/NetworkOptionsPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'HarvestSitesPage', '/site/HarvestSitesPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'AddClassificationsPage', '/networkAdmin/AddClassificationsPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManageClassificationsPage', '/networkAdmin/ManageClassificationsPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'CommentReviewPage', '/networkAdmin/CommentReviewPage.xhtml', null, 2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManageDataversesPage', '/networkAdmin/ManageDataversesPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditHarvestSitePage', '/site/EditHarvestSitePage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditNetworkNamePage', '/networkAdmin/EditNetworkNamePage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'NetworkPrivilegedUsersPage', '/networkAdmin/NetworkPrivilegedUsersPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'AllUsersPage', '/networkAdmin/AllUsersPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditNetworkAnnouncementsPage', '/networkAdmin/EditNetworkAnnouncementsPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditNetworkBannerFooterPage', '/networkAdmin/EditNetworkBannerFooterPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditExportSchedulePage', '/networkAdmin/EditExportSchedulePage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditOAISetPage', '/networkAdmin/EditOAISetPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditNetworkDownloadUseTermsPage', '/networkAdmin/EditNetworkDownloadUseTermsPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditNetworkDepositUseTermsPage', '/networkAdmin/EditNetworkDepositUseTermsPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditAccountUseTermsPage', '/networkAdmin/EditAccountUseTermsPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditUserGroupPage', '/networkAdmin/EditUserGroupPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'UserGroupsPage', '/networkAdmin/UserGroupsPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ImportStudyPage', '/networkAdmin/ImportStudyPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'UtilitiesPage', '/networkAdmin/UtilitiesPage.xhtml', null, 2 );

ALTER TABLE pagedef ENABLE TRIGGER ALL;


--
-- TOC entry 1821 (class 0 OID 113878)
-- Dependencies: 1285
-- Data for Name: search_result_fields; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE search_result_fields DISABLE TRIGGER ALL;



ALTER TABLE search_result_fields ENABLE TRIGGER ALL;


--
-- TOC entry 1826 (class 0 OID 113907)
-- Dependencies: 1293
-- Data for Name: study_studyfield; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE study_studyfield DISABLE TRIGGER ALL;



ALTER TABLE study_studyfield ENABLE TRIGGER ALL;

--
-- TOC entry 1817 (class 0 OID 113859)
-- Dependencies: 1280
-- Data for Name: study_usergroup; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE study_usergroup DISABLE TRIGGER ALL;



ALTER TABLE study_usergroup ENABLE TRIGGER ALL;

--
-- TOC entry 1812 (class 0 OID 113829)
-- Dependencies: 1273
-- Data for Name: studyfield; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE studyfield DISABLE TRIGGER ALL;
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (1, 'Title', 'title', TRUE, TRUE, TRUE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (2, 'Study ID', 'studyId', TRUE, TRUE, TRUE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (3, 'Author', 'authorName', TRUE, TRUE, TRUE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (4, 'Author Affiliation', 'authorAffiliation', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (5, 'Producer', 'producerName', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (6, 'Producer URL', 'producerURL', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (7, 'URL to Producer Logo', 'producerLogo', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (8, 'Producer Name Abbreviation', 'producerAbbreviation', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (9, 'Production Date', 'productionDate', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (10, 'Software', 'softwareName', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (11, 'Software Version', 'softwareVersion', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (12, 'Funding Agency', 'fundingAgency', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (13, 'Grant Number', 'grantNumber', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (14, 'Grant Number Agency', 'grantNumberAgency', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (15, '', 'distributorName', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (16, '', 'distributorURL', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (17, '', 'distributorLogo', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (18, '', 'distributionDate', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (19, '', 'distributorContact', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (20, '', 'distributorContactAffiliation', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (21, '', 'distributorContactEmail', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (22, '', 'depositor', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (23, '', 'dateOfDeposit', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (24, '', 'seriesName', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (25, '', 'seriesInformation', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (26, '', 'studyVersion', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (27, '', 'keywordValue', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (28, '', 'keywordVocab', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (29, '', 'keywordVocabURI', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (30, '', 'topicClassValue', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (31, '', 'topicClassVocab', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (32, '', 'topicClassVocabURI', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (33, '', 'abstractText', FALSE, TRUE, TRUE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (34, '', 'abstractDate', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (35, '', 'timePeriodCoveredStart', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (36, '', 'timePeriodCoveredEnd', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (37, '', 'dateOfCollectionStart', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (38, '', 'dateOfCollectionEnd', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (39, '', 'country', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (40, '', 'geographicCoverage', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (41, '', 'geographicUnit', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (42, '', 'unitOfAnalysis', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (43, '', 'universe', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (44, '', 'kindOfData', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (45, '', 'timeMethod', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (46, '', 'dataCollector', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (47, '', 'frequencyOfDataCollection', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (48, '', 'samplingProcedure', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (49, '', 'deviationsFromSampleDesign', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (50, '', 'collectionMode', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (51, '', 'researchInstrument', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (52, '', 'dataSources', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (53, '', 'originOfSources', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (54, '', 'characteristicOfSources', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (55, '', 'accessToSources', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (56, '', 'dataCollectionSituation', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (57, '', 'actionsToMinimizeLoss', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (58, '', 'controlOperations', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (59, '', 'weighting', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (60, '', 'cleaningOperations', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (61, '', 'studyLevelErrorNotes', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (62, '', 'responseRate', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (63, '', 'samplingErrorEstimates', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (64, '', 'otherDataAppraisal', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (65, '', 'placeOfAccess', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (66, '', 'originalArchive', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (67, '', 'availabilityStatus', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (68, '', 'collectionSize', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (69, '', 'studyCompletion', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (70, '', 'confidentialityDeclaration', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (71, '', 'specialPermissions', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (72, '', 'restrictions', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (73, '', 'contact', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (74, '', 'citationRequirements', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (75, '', 'depositorRequirements', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (76, '', 'conditions', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (77, '', 'disclaimer', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (78, '', 'relatedMaterial', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (79, '', 'relatedPublications', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (80, '', 'relatedStudies', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (81, '', 'otherReferences', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (82, '', 'NotesText', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (83, '', 'NotesInformationType', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (84, '', 'NotesInformationSubject', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (85, '', 'otherId', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (86, '', 'otherIdAgency', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (87, '', 'productionPlace', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (88, '', 'numberOfFiles', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (89, '', 'replicationFor', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (90, '', 'subTitle', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (91, '', 'versionDate', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (92, '', 'westLongitude', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (93, '', 'eastLongitude', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (94, '', 'northLatitude', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (95, '', 'southLatitude', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (96, '', 'producerAffiliation', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (97, '', 'distributorAffiliation', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id, description, name,basicSearchField,advancedSearchField, searchResultField, dcmField) VALUES (98, '', 'distributorAbbreviation', FALSE, FALSE, FALSE, FALSE );




ALTER TABLE studyfield ENABLE TRIGGER ALL;





--
-- TOC entry 1811 (class 0 OID 113819)
-- Dependencies: 1271
-- Data for Name: studyfile; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE studyfile DISABLE TRIGGER ALL;



ALTER TABLE studyfile ENABLE TRIGGER ALL;

--
-- TOC entry 1810 (class 0 OID 113813)
-- Dependencies: 1269
-- Data for Name: studyfile_usergroup; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE studyfile_usergroup DISABLE TRIGGER ALL;



ALTER TABLE studyfile_usergroup ENABLE TRIGGER ALL;

--
-- TOC entry 1835 (class 0 OID 113964)
-- Dependencies: 1307
-- Data for Name: summary_fields; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE summary_fields DISABLE TRIGGER ALL;



ALTER TABLE summary_fields ENABLE TRIGGER ALL;

--
-- TOC entry 1809 (class 0 OID 113808)
-- Dependencies: 1268
-- Data for Name: template; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE "template" DISABLE TRIGGER ALL;



ALTER TABLE "template" ENABLE TRIGGER ALL;

--
-- TOC entry 1807 (class 0 OID 113797)
-- Dependencies: 1265
-- Data for Name: templatefield; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE templatefield DISABLE TRIGGER ALL;



ALTER TABLE templatefield ENABLE TRIGGER ALL;

--
-- TOC entry 1815 (class 0 OID 113850)
-- Dependencies: 1278
-- Data for Name: templatefieldselectvalue; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE templatefieldselectvalue DISABLE TRIGGER ALL;



ALTER TABLE templatefieldselectvalue ENABLE TRIGGER ALL;

--
-- TOC entry 1832 (class 0 OID 113945)
-- Dependencies: 1303
-- Data for Name: usergroup; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE usergroup DISABLE TRIGGER ALL;



ALTER TABLE usergroup ENABLE TRIGGER ALL;


--
-- TOC entry 1831 (class 0 OID 113939)
-- Dependencies: 1301
-- Data for Name: vdc_adv_search_fields; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE vdc_adv_search_fields DISABLE TRIGGER ALL;



ALTER TABLE vdc_adv_search_fields ENABLE TRIGGER ALL;

--
-- TOC entry 1801 (class 0 OID 113756)
-- Dependencies: 1255
-- Data for Name: vdc_any_search_fields; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE vdc_any_search_fields DISABLE TRIGGER ALL;



ALTER TABLE vdc_any_search_fields ENABLE TRIGGER ALL;


--
-- TOC entry 1808 (class 0 OID 113802)
-- Dependencies: 1266
-- Data for Name: vdc_usergroup; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE vdc_usergroup DISABLE TRIGGER ALL;



ALTER TABLE vdc_usergroup ENABLE TRIGGER ALL;


--
-- TOC entry 1830 (class 0 OID 113934)
-- Dependencies: 1300
-- Data for Name: vdcgroup; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE vdcgroup DISABLE TRIGGER ALL;



ALTER TABLE vdcgroup ENABLE TRIGGER ALL;

--
-- TOC entry 1828 (class 0 OID 113921)
-- Dependencies: 1296
-- Data for Name: vdcgrouprelationship; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE vdcgrouprelationship DISABLE TRIGGER ALL;



ALTER TABLE vdcgrouprelationship ENABLE TRIGGER ALL;





--
-- TOC entry 1827 (class 0 OID 113913)
-- Dependencies: 1295
-- Data for Name: vdcuser; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE vdcuser DISABLE TRIGGER ALL;



ALTER TABLE vdcuser ENABLE TRIGGER ALL;

--
-- TOC entry 1834 (class 0 OID 113960)
-- Dependencies: 1306
-- Data for Name: vdcuser_usergroup; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE vdcuser_usergroup DISABLE TRIGGER ALL;



ALTER TABLE vdcuser_usergroup ENABLE TRIGGER ALL;


ALTER TABLE variableintervaltype DISABLE TRIGGER ALL;

INSERT INTO variableintervaltype (id, name ) VALUES (1, 'discrete');
INSERT INTO variableintervaltype (id, name ) VALUES(2, 'continuous');
INSERT INTO variableintervaltype (id, name ) VALUES(3, 'nominal');
INSERT INTO variableintervaltype (id, name ) VALUES(4, 'dichotomous');

ALTER TABLE variableintervaltype ENABLE TRIGGER ALL;


ALTER TABLE variableformattype DISABLE TRIGGER ALL;

INSERT INTO variableformattype (id, name ) VALUES (1, 'numeric');
INSERT INTO variableformattype (id, name ) VALUES(2, 'character');

ALTER TABLE variableformattype ENABLE TRIGGER ALL;


ALTER TABLE variablerangetype DISABLE TRIGGER ALL;

INSERT INTO variablerangetype (id, name ) VALUES(1, 'min');
INSERT INTO variablerangetype (id, name ) VALUES(2, 'max');
INSERT INTO variablerangetype (id, name ) VALUES(3, 'min exclusive');
INSERT INTO variablerangetype (id, name ) VALUES(4, 'max exclusive');
INSERT INTO variablerangetype (id, name ) VALUES(5, 'point');

ALTER TABLE variablerangetype ENABLE TRIGGER ALL;

ALTER TABLE summarystatistictype DISABLE TRIGGER ALL;

INSERT INTO summarystatistictype (id, name ) VALUES(1, 'mean');
INSERT INTO summarystatistictype (id, name ) VALUES(2, 'medn');
INSERT INTO summarystatistictype (id, name ) VALUES(3, 'mode');
INSERT INTO summarystatistictype (id, name ) VALUES(4, 'min');
INSERT INTO summarystatistictype (id, name ) VALUES(5, 'max');
INSERT INTO summarystatistictype (id, name ) VALUES(6, 'stdev');
INSERT INTO summarystatistictype (id, name ) VALUES(7, 'vald');
INSERT INTO summarystatistictype (id, name ) VALUES(8, 'invd');

ALTER TABLE variablerangetype ENABLE TRIGGER ALL;

ALTER TABLE vdcuser DISABLE TRIGGER ALL;

insert into vdcuser(id, version, email,  firstname, lastname, username,  encryptedpassword, networkRole_id,active, agreedtermsofuse ) VALUES ( 1, 1, 'dataverse@lists.hmdc.harvard.edu','Network','Admin', 'networkAdmin' ,'tf0bLmzOFx5JrBhe2EIraS5GBnI=' ,2,true, true);

ALTER TABLE vdcuser ENABLE TRIGGER ALL;

--
-- TOC entry 1809 (class 0 OID 113808)
-- Dependencies: 1268
-- Data for Name: template; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE "metadata" DISABLE TRIGGER ALL;
-- Default metadata - contains no metadata values
INSERT INTO metadata( id, version ) VALUES ( 1, 1);

ALTER TABLE "metadata" ENABLE TRIGGER ALL;


ALTER TABLE "template" DISABLE TRIGGER ALL;

INSERT INTO template( id, version, name,metadata_id) VALUES (1, 1, 'Dataverse Network Default Template',1);

ALTER TABLE "template" ENABLE TRIGGER ALL;

--
-- TOC entry 1824 (class 0 OID 113895)
-- Dependencies: 1290
-- Data for Name: vdcnetwork; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE vdcnetwork DISABLE TRIGGER ALL;

INSERT INTO vdcnetwork (id, version, name, networkpageheader, networkpagefooter, announcements, displayannouncements, aboutthisdataversenetwork, contactemail, defaultvdcheader, defaultvdcfooter, defaultvdcabouttext, defaultvdcannouncements, displayvdcannouncements, displayvdcrecentstudies, defaulttemplate_id, allowcreaterequest, defaultnetworkadmin_id,protocol,authority,handleregistration,termsofuseenabled, deposittermsofuseenabled, downloadtermsofuseenabled, defaultdisplaynumber, exportperiod, exporthourofday) VALUES (1, 1, '[Your]', ' ', ' ', 'A description of your Dataverse Network or announcements may be added here. Use Network Options to edit or remove this text.', TRUE, 'This About page is not used anymore in the DVN application.', 'dataverse@lists.hmdc.harvard.edu', ' ', ' ', 'This About page is not used anymore in the DVN application.', '', TRUE, TRUE, 1, FALSE,1,'hdl','TEST',false,false,false,false,16,'daily',3);

update vdcnetwork set defaultvdcheader='<style type="text/css">
body {margin:0; padding:0;}
</style>
<div style="width:100%; height:40px; background: url(/dvn/resources/images/customizationpattern.png) repeat-x left -35px #698DA2;"></div>
<div style="margin:0 auto; max-width:1000px;">';

update vdcnetwork set defaultvdcfooter='</div>';



update vdcnetwork set  requireDVDescription = false,
 requireDVaffiliation = false,
 requireDVclassification = false,
 requireDVstudiesforrelease = false;

ALTER TABLE vdcnetwork ENABLE TRIGGER ALL;


--
-- TOC entry 1807 (class 0 OID 113797)
-- Dependencies: 1265
-- Data for Name: templatefield; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE templatefield DISABLE TRIGGER ALL;

INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(1,1,1,'required');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(2,1,2,'required');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(3,1,3,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(4,1,4,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(5,1,5,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(6,1,6,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(7,1,7,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(8,1,8,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(9,1,9,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(10,1,10,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(11,1,11,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(12,1,12,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(13,1,13,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(14,1,14,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(15,1,15,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(16,1,16,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(17,1,17,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(18,1,18,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(19,1,19,'recommended');

INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(20,1,20,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(21,1,21,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(22,1,22,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(23,1,23,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(24,1,24,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(25,1,25,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(26,1,26,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(27,1,27,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(28,1,28,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(29,1,29,'optional');

INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(30,1,30,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(31,1,31,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(32,1,32,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(33,1,33,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(34,1,34,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(35,1,35,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(36,1,36,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(37,1,37,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(38,1,38,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(39,1,39,'recommended');

INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(40,1,40,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(41,1,41,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(42,1,42,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(43,1,43,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(44,1,44,'recommended');

INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(45,1,45,'optional',1);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(46,1,46,'optional',2);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(47,1,47,'optional',3);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(48,1,48,'optional',4);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(49,1,49,'optional',5);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(50,1,50,'optional',6);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(51,1,51,'optional',7);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(52,1,52,'optional',8);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(53,1,53,'optional',9);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(54,1,54,'optional',10);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(55,1,55,'optional',11);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(56,1,56,'optional',12);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(57,1,57,'optional',13);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(58,1,58,'optional',14);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(59,1,59,'optional',15);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(60,1,60,'optional',16);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(61,1,61,'optional',17);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(62,1,62,'optional',18);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(63,1,63,'optional',19);
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring, dcmsortorder) VALUES(64,1,64,'optional',20);

INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(65,1,65,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(66,1,66,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(67,1,67,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(68,1,68,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(69,1,69,'optional');

INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(70,1,70,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(71,1,71,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(72,1,72,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(73,1,73,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(74,1,74,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(75,1,75,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(76,1,76,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(77,1,77,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(78,1,78,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(79,1,79,'recommended');

INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(80,1,80,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(81,1,81,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(82,1,82,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(83,1,83,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(84,1,84,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(85,1,85,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(86,1,86,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(87,1,87,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(88,1,88,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(89,1,89,'recommended');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(90,1,90,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(91,1,91,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(92,1,92,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(93,1,93,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(94,1,94,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(95,1,95,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(96,1,96,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(97,1,97,'optional');
INSERT INTO templatefield(id, template_id, studyfield_id, fieldinputlevelstring) VALUES(98,1,98,'optional');


SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('templatefield', 'id'), 100, false);


ALTER TABLE templatefield ENABLE TRIGGER ALL;

--
-- TOC entry 1815 (class 0 OID 113850)
-- Dependencies: 1278
-- Data for Name: templatefieldselectvalue; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE templatefieldselectvalue DISABLE TRIGGER ALL;



ALTER TABLE templatefieldselectvalue ENABLE TRIGGER ALL;


--
-- TOC entry 1814 (class 0 OID 113843)
-- Dependencies: 1276
-- Data for Name: templatefilecategory; Type: TABLE DATA; Schema: public; Owner: dvnApp
--

ALTER TABLE templatefilecategory DISABLE TRIGGER ALL;

INSERT INTO templatefilecategory(id, template_id, name, displayorder) VALUES(1,1,'Documentation',1);
INSERT INTO templatefilecategory(id, template_id, name, displayorder) VALUES(2,1,'Data Files',2);

ALTER TABLE templatefilecategory ENABLE TRIGGER ALL;

--
-- TOC entry 1855 (class 0 OID 0)
-- Dependencies: 1275
-- Name: templatefilecategory_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dvnApp
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('templatefilecategory', 'id'), 5, false);


-- Completed on 2006-09-19 16:05:06 Eastern Standard Time

--
-- PostgreSQL database dump complete
--


-- Sequence: studyid_seq

-- DROP SEQUENCE studyid_seq;

CREATE SEQUENCE studyid_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 10000
  CACHE 1;
ALTER TABLE studyid_seq OWNER TO "dvnApp";

-- Sequence: filesystemname_seq

-- DROP SEQUENCE filesystemname_seq;

CREATE SEQUENCE filesystemname_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 2
  CACHE 1;
ALTER TABLE filesystemname_seq OWNER TO "dvnApp";


INSERT INTO DataFileFormatType (id, value, name, mimeType) VALUES (1, 'D02', 'Splus', 'application/x-rlang-transport');
INSERT INTO DataFileFormatType (id, value, name, mimeType) VALUES (2, 'D03', 'Stata', 'application/x-stata');
INSERT INTO DataFileFormatType (id, value, name, mimeType) VALUES (3, 'D04', 'R', 'application/x-R-2');

INSERT INTO harvestformattype (id, metadataprefix, name, stylesheetfilename) VALUES (0, 'ddi', 'DDI', null);
INSERT INTO harvestformattype (id, metadataprefix, name, stylesheetfilename) VALUES (1, 'oai_etdms', 'MIF', 'mif2ddi.xsl');
INSERT INTO harvestformattype (id, metadataprefix, name, stylesheetfilename) VALUES (2, 'oai_dc', 'DC', 'oai_dc2ddi.xsl');
INSERT INTO harvestformattype (id, metadataprefix, name, stylesheetfilename) VALUES (3, 'oai_fgdc', 'FGDC', 'fgdc2ddi.xsl');

create index datavariable_id_index on  datavariable (id);
create index summarystatistic_id_index on  summarystatistic (id);
create index summarystatistic_datavariable_id_index on  summarystatistic (datavariable_id);
create index variablecategory_id_index on  variablecategory (id);
create index variablecategory_datavariable_id_index on  variablecategory (datavariable_id);
create index variablerange_id_index on  variablerange (id);
create index study_id_index on study(id);
create index study_owner_id_index on study(owner_id);
create index weightedvarrelationship_id_index on  weightedvarrelationship (weighted_variable_id,variable_id);
create index studyfile_id_index on studyfile(id);
create index datavariable_datatable_id_index on datavariable(datatable_id);
create index variablerange_datavariable_id_index on  variablerange (datavariable_id);
create index metadata_id_index on metadata(id);
create index studyabstract_metadata_id_index on studyabstract(metadata_id);
create index studyauthor_metadata_id_index on studyauthor(metadata_id);
create index studydistributor_metadata_id_index on studydistributor(metadata_id);
create index studygeobounding_metadata_id_index on studygeobounding(metadata_id);
create index studygrant_metadata_id_index on studygrant(metadata_id);
create index studykeyword_metadata_id_index on studykeyword(metadata_id);
create index studynote_metadata_id_index on studynote(metadata_id);
create index studyotherid_metadata_id_index on studyotherid(metadata_id);
create index studyotherref_metadata_id_index on studyotherref(metadata_id);
create index studyproducer_metadata_id_index on studyproducer(metadata_id);
create index studyrelmaterial_metadata_id_index on studyrelmaterial(metadata_id);
create index studyrelpublication_metadata_id_index on studyrelpublication(metadata_id);
create index studyrelstudy_metadata_id_index on studyrelstudy(metadata_id);
create index studysoftware_metadata_id_index on studysoftware(metadata_id);
create index studytopicclass_metadata_id_index on studytopicclass(metadata_id);
create index template_metadata_id_index on template(metadata_id);
create index studyfileactivity_id_index on studyfileactivity(id);
create index studyfileactivity_studyfile_id_index on studyfileactivity(studyfile_id);
create index studyfileactivity_study_id_index on studyfileactivity(study_id);





INSERT INTO vdcnetworkstats (id,studycount,filecount) values (1,0,0);

 insert into licensetype (id, shortname, name, licenseurl, rdfurl, imageurl) values ( 1, 'cc by', 'CC Attribution (cc by)', 'http://creativecommons.org/licenses/by/3.0/', 'http://creativecommons.org/licenses/by/3.0/rdf', 'http://i.creativecommons.org/l/by/3.0/88x31.png' );
-- removed until we support cc0
--insert into licensetype (id, shortname, name, licenseurl, rdfurl, imageurl) values ( 2, 'cc0','CC Zero (cc0)','http://creativecommons.org/publicdomain/zero/1.0/','http://creativecommons.org/publicdomain/zero/1.0/rdf','http://i.creativecommons.org/l/zero/1.0/88x31.png');
insert into licensetype (id, shortname, name, licenseurl, rdfurl, imageurl) values ( 3, 'cc by-sa','CC Attribution Share Alike (cc by-sa)','http://creativecommons.org/licenses/by-sa/3.0/', 'http://creativecommons.org/licenses/by-sa/3.0/rdf', 'http://i.creativecommons.org/l/by-sa/3.0/88x31.png' );
insert into licensetype (id, shortname, name, licenseurl, rdfurl, imageurl) values ( 4, 'cc by-nd','CC Attribution No Derivatives (cc by-nd)','http://creativecommons.org/licenses/by-nd/3.0/', 'http://creativecommons.org/licenses/by-nd/3.0/rdf', 'http://i.creativecommons.org/l/by-nd/3.0/88x31.png' );
insert into licensetype (id, shortname, name, licenseurl, rdfurl, imageurl) values ( 5, 'cc by-nc','CC Attribution Non-Commercial (cc by-nc)','http://creativecommons.org/licenses/by-nc/3.0/', 'http://creativecommons.org/licenses/by-nc/3.0/rdf', 'http://i.creativecommons.org/l/by-nc/3.0/88x31.png' );
insert into licensetype (id, shortname, name, licenseurl, rdfurl, imageurl) values ( 6, 'cc by-nc-sa','CC Attribution Non-Commercial Share Alike (cc by-nc-sa)','http://creativecommons.org/licenses/by-nc-sa/3.0/', 'http://creativecommons.org/licenses/by-nc-sa/3.0/rdf', 'http://i.creativecommons.org/l/by-nc-sa/3.0/88x31.png' );
insert into licensetype (id, shortname, name, licenseurl, rdfurl, imageurl) values ( 7, 'cc by-nc-nd','CC Attribution Non-Commercial No Derivatives (cc by-nc-nd)','http://creativecommons.org/licenses/by-nc-sa/3.0/', 'http://creativecommons.org/licenses/by-nc-sa/3.0/rdf', 'http://i.creativecommons.org/l/by-nc-sa/3.0/88x31.png' );
