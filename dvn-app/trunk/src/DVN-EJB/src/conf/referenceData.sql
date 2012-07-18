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
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'MyDataversePage','/networkAdmin/MyDataversePage.xhtml',null,null );

-- Curator Role
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditCollectionPage','/collection/EditCollectionPage.xhtml',2,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManageCollectionsPage','/collection/ManageCollectionsPage.xhtml',2,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'StudyPermissionsPage','/study/StudyPermissionsPage.xhtml',2,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'DeaccessionStudyPage', '/study/DeaccessionStudyPage.xhtml', 2, null );

INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManageTemplatesPage', '/admin/ManageTemplatesPage.xhtml', 2,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'TemplateFormPage','/study/TemplateFormPage.xhtml',2,2 );

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
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'EditGuestbookQuestionnairePage', '/admin/EditGuestbookQuestionnairePage.xhtml', 3,null );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'GuestBookResponseDataPage', '/admin/GuestBookResponseDataPage.xhtml', 3,2 );
-- Pages that require Network Role authorization
-- Creator Role 
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'AddSitePage', '/site/AddSitePage.xhtml', null,1 );
-- Admin Role
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'NetworkOptionsPage', '/networkAdmin/NetworkOptionsPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'HarvestSitesPage', '/site/HarvestSitesPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'AddClassificationsPage', '/networkAdmin/AddClassificationsPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManageClassificationsPage', '/networkAdmin/ManageClassificationsPage.xhtml', null,2 );
INSERT INTO pagedef ( name, path, role_id, networkrole_id ) VALUES ( 'ManageControlledVocabularyPage', '/admin/ManageControlledVocabularyPage.xhtml', null,2 );
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

INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (1, 'Title', 'Title', 'title', TRUE, TRUE, TRUE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (2, 'Study ID', 'Study ID', 'studyId', TRUE, TRUE, TRUE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (3, 'Author', 'Author', 'author', TRUE, TRUE, TRUE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (4, 'Author Affiliation', 'Author Affiliation', 'authorAffiliation', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (5, 'Producer', 'Producer', 'producer', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (6, 'Producer URL', 'Producer URL', 'producerURL', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (7, 'URL to Producer Logo', 'URL to Producer Logo', 'producerLogo', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (8, 'Producer Name Abbreviation', 'Producer Name Abbreviation', 'producerAbbreviation', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (9, 'Production Date', 'Production Date', 'productionDate', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (10, 'Software', 'Software', 'software', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (11, 'Software Version', 'Software Version', 'softwareVersion', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (12, 'Funding Agency', 'Funding Agency', 'fundingAgency', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (13, 'Grant Number', 'Grant Number', 'grantNumber', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (14, 'Grant Number Agency', 'Grant Number Agency', 'grantNumberAgency', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (15, '', '', 'distributor', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (16, '', '', 'distributorURL', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (17, '', '', 'distributorLogo', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (18, '', '', 'distributionDate', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (19, '', '', 'distributorContact', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (20, '', '', 'distributorContactAffiliation', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (21, '', '', 'distributorContactEmail', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (22, '', '', 'depositor', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (23, '', '', 'dateOfDeposit', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (24, '', '', 'series', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (25, '', '', 'seriesInformation', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (26, '', '', 'studyVersion', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (27, '', '', 'keyword', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (28, '', '', 'keywordVocab', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (29, '', '', 'keywordVocabURI', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (30, '', '', 'topicClassification', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (31, '', '', 'topicClassVocab', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (32, '', '', 'topicClassVocabURI', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (33, '', '', 'description', FALSE, TRUE, TRUE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (34, '', '', 'descriptionDate', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (35, '', '', 'timePeriodCoveredStart', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (36, '', '', 'timePeriodCoveredEnd', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (37, '', '', 'dateOfCollectionStart', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (38, '', '', 'dateOfCollectionEnd', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (39, '', '', 'country', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (40, '', '', 'geographicCoverage', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (41, '', '', 'geographicUnit', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (42, '', '', 'unitOfAnalysis', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (43, '', '', 'universe', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (44, '', '', 'kindOfData', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (45, '', '', 'timeMethod', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (46, '', '', 'dataCollector', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (47, '', '', 'frequencyOfDataCollection', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (48, '', '', 'samplingProcedure', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (49, '', '', 'deviationsFromSampleDesign', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (50, '', '', 'collectionMode', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (51, '', '', 'researchInstrument', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (52, '', '', 'dataSources', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (53, '', '', 'originOfSources', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (54, '', '', 'characteristicOfSources', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (55, '', '', 'accessToSources', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (56, '', '', 'dataCollectionSituation', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (57, '', '', 'actionsToMinimizeLoss', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (58, '', '', 'controlOperations', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (59, '', '', 'weighting', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (60, '', '', 'cleaningOperations', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (61, '', '', 'studyLevelErrorNotes', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (62, '', '', 'responseRate', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (63, '', '', 'samplingErrorEstimates', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (64, '', '', 'otherDataAppraisal', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (65, '', '', 'placeOfAccess', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (66, '', '', 'originalArchive', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (67, '', '', 'availabilityStatus', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (68, '', '', 'collectionSize', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (69, '', '', 'studyCompletion', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (70, '', '', 'confidentialityDeclaration', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (71, '', '', 'specialPermissions', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (72, '', '', 'restrictions', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (73, '', '', 'contact', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (74, '', '', 'citationRequirements', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (75, '', '', 'depositorRequirements', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (76, '', '', 'conditions', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (77, '', '', 'disclaimer', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (78, '', '', 'relatedMaterial', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (79, '', '', 'publication', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (80, '', '', 'relatedStudies', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (81, '', '', 'otherReferences', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (82, '', '', 'notesText', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (83, '', '', 'note', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (84, '', '', 'notesInformationSubject', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (85, '', '', 'otherId', FALSE, TRUE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (86, '', '', 'otherIdAgency', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (87, '', '', 'productionPlace', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (88, '', '', 'numberOfFiles', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (89, '', '', 'publicationReplicationData', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (90, '', '', 'subTitle', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (91, '', '', 'versionDate', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (92, '', '', 'geographicBoundingBox', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (93, '', '', 'eastLongitude', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (94, '', '', 'northLatitude', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (95, '', '', 'southLatitude', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (96, '', '', 'producerAffiliation', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (97, '', '', 'distributorAffiliation', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (98, '', '', 'distributorAbbreviation', FALSE, FALSE, FALSE, FALSE );

INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (99, '', '', 'authorName', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (100, '', '', 'producerName', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (101, '', '', 'distributorName', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (102, '', '', 'distributorContactName', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (103, '', '', 'descriptionText', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (104, '', '', 'keywordValue', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (105, '', '', 'topicClassValue', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (106, '', '', 'otherIdValue', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (107, '', '', 'softwareName', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (108, '', '', 'grantNumberValue', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (109, '', '', 'seriesName', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (110, '', '', 'studyVersionValue', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (111, '', '', 'westLongitude', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (112, '', '', 'noteInformationType', FALSE, FALSE, FALSE, FALSE );

INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (113, '', '', 'publicationCitation', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (114, '', '', 'publicationIDType', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (115, '', '', 'publicationIDNumber', FALSE, FALSE, FALSE, FALSE );
INSERT INTO studyfield (id,title,description, name,basicSearchField,advancedSearchField, searchResultField, customField) VALUES (116, '', '', 'publicationURL', FALSE, FALSE, FALSE, FALSE );

--set the parent child relationship
update studyfield set parentstudyfield_id = 3 where id = 99;
update studyfield set parentstudyfield_id = 3 where id = 4;

update studyfield set parentstudyfield_id = 5 where id = 100;
update studyfield set parentstudyfield_id = 5 where id = 6;
update studyfield set parentstudyfield_id = 5 where id = 7;
update studyfield set parentstudyfield_id = 5 where id = 8;
update studyfield set parentstudyfield_id = 5 where id = 96;

update studyfield set parentstudyfield_id = 15 where id = 101;
update studyfield set parentstudyfield_id = 15 where id = 16;
update studyfield set parentstudyfield_id = 15 where id = 17;
update studyfield set parentstudyfield_id = 15 where id = 97;
update studyfield set parentstudyfield_id = 15 where id = 98;

update studyfield set parentstudyfield_id = 19 where id = 102;
update studyfield set parentstudyfield_id = 19 where id = 20;
update studyfield set parentstudyfield_id = 19 where id = 21;

update studyfield set parentstudyfield_id = 33 where id = 103;
update studyfield set parentstudyfield_id = 33 where id = 34;

update studyfield set parentstudyfield_id = 27 where id = 104;
update studyfield set parentstudyfield_id = 27 where id = 28;
update studyfield set parentstudyfield_id = 27 where id = 29;

update studyfield set parentstudyfield_id = 30 where id = 105;
update studyfield set parentstudyfield_id = 30 where id = 31;
update studyfield set parentstudyfield_id = 30 where id = 32;

update studyfield set parentstudyfield_id = 85 where id = 106;
update studyfield set parentstudyfield_id = 85 where id = 86;

update studyfield set parentstudyfield_id = 10 where id = 107;
update studyfield set parentstudyfield_id = 10 where id = 11;

update studyfield set parentstudyfield_id = 13 where id = 108;
update studyfield set parentstudyfield_id = 13 where id = 14;

update studyfield set parentstudyfield_id = 24 where id = 109;
update studyfield set parentstudyfield_id = 24 where id = 25;

update studyfield set parentstudyfield_id = 26 where id = 110;
update studyfield set parentstudyfield_id = 26 where id = 91;

update studyfield set parentstudyfield_id = 92 where id = 111;
update studyfield set parentstudyfield_id = 92 where id = 93;
update studyfield set parentstudyfield_id = 92 where id = 94;
update studyfield set parentstudyfield_id = 92 where id = 95;

update studyfield set parentstudyfield_id = 83 where id = 112;
update studyfield set parentstudyfield_id = 83 where id = 82;
update studyfield set parentstudyfield_id = 83 where id = 84;

update studyfield set parentstudyfield_id = 79 where id = 113;
update studyfield set parentstudyfield_id = 79 where id = 114;
update studyfield set parentstudyfield_id = 79 where id = 115;
update studyfield set parentstudyfield_id = 79 where id = 116;
update studyfield set parentstudyfield_id = 79 where id = 89;


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

INSERT INTO template( id, version, name,metadata_id,enabled) VALUES (1, 1, 'Dataverse Network Default Template',1,true);

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

INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(1,1,1,1,'required',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(2,1,1,2,'required',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(3,1,1,3,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(4,1,1,4,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(5,1,1,5,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(6,1,1,6,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(7,1,1,7,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(8,1,1,8,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(9,1,1,9,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(10,1,1,10,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(11,1,1,11,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(12,1,1,12,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(13,1,1,13,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(14,1,1,14,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(15,1,1,15,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(16,1,1,16,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(17,1,1,17,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(18,1,1,18,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(19,1,1,19,'recommended',-1);

INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(20,1,1,20,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(21,1,1,21,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(22,1,1,22,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(23,1,1,23,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(24,1,1,24,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(25,1,1,25,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(26,1,1,26,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(27,1,1,27,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(28,1,1,28,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(29,1,1,29,'optional',-1);

INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(30,1,1,30,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(31,1,1,31,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(32,1,1,32,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(33,1,1,33,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(34,1,1,34,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(35,1,1,35,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(36,1,1,36,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(37,1,1,37,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(38,1,1,38,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(39,1,1,39,'recommended',-1);

INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(40,1,1,40,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(41,1,1,41,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(42,1,1,42,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(43,1,1,43,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(44,1,1,44,'recommended',-1);

INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(45,1,1,45,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(46,1,1,46,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(47,1,1,47,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(48,1,1,48,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(49,1,1,49,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(50,1,1,50,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(51,1,1,51,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(52,1,1,52,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(53,1,1,53,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(54,1,1,54,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(55,1,1,55,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(56,1,1,56,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(57,1,1,57,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(58,1,1,58,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(59,1,1,59,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(60,1,1,60,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(61,1,1,61,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(62,1,1,62,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(63,1,1,63,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(64,1,1,64,'optional',-1);

INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(65,1,1,65,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(66,1,1,66,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(67,1,1,67,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(68,1,1,68,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(69,1,1,69,'optional',-1);

INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(70,1,1,70,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(71,1,1,71,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(72,1,1,72,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(73,1,1,73,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(74,1,1,74,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(75,1,1,75,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(76,1,1,76,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(77,1,1,77,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(78,1,1,78,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(79,1,1,79,'recommended',-1);

INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(80,1,1,80,'recommended',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(81,1,1,81,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(82,1,1,82,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(83,1,1,83,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(84,1,1,84,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(85,1,1,85,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(86,1,1,86,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(87,1,1,87,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(88,1,1,88,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(89,1,1,89,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(90,1,1,90,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(91,1,1,91,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(92,1,1,92,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(93,1,1,93,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(94,1,1,94,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(95,1,1,95,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(96,1,1,96,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(97,1,1,97,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(98,1,1,98,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(99,1,1,99,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(100,1,1,100,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(101,1,1,101,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(102,1,1,102,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(103,1,1,103,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(104,1,1,104,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(105,1,1,105,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(106,1,1,106,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(107,1,1,107,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(108,1,1,108,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(109,1,1,109,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(110,1,1,110,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(111,1,1,111,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(112,1,1,112,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(113,1,1,113,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(114,1,1,114,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(115,1,1,115,'optional',-1);
INSERT INTO templatefield(id, version, template_id, studyfield_id, fieldinputlevelstring, displayorder) VALUES(116,1,1,116,'optional',-1);

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('templatefield', 'id'), 150, false);


ALTER TABLE templatefield ENABLE TRIGGER ALL;



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

INSERT INTO metadataformattype (id, name, mimetype, namespace, formatschema, partialexcludesupported, partialselectsupported) VALUES (1, 'ddi', 'application/xml', 'http://www.icpsr.umich.edu/DDI', 'http://www.icpsr.umich.edu/DDI/Version2-0.xsd', true, true);
INSERT INTO metadataformattype (id, name, mimetype, namespace, formatschema, partialexcludesupported, partialselectsupported) VALUES (2, 'oai_dc', 'application/xml', 'http://www.openarchives.org/OAI/2.0/oai_dc/', 'http://www.openarchives.org/OAI/2.0/oai_dc.xsd', false, false);
INSERT INTO metadataformattype (id, name, mimetype, namespace, formatschema, partialexcludesupported, partialselectsupported) VALUES (3, 'marc', 'application/octet-stream', 'http://www.loc.gov/marc/', 'MARC 21', false, false);
