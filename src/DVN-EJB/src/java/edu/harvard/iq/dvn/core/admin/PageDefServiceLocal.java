/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
/*
 * PageDefServiceLocal.java
 *
 * Created on November 7, 2006, 4:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;

/**
 *
 * @author Ellen Kraffmiller
 */
public interface PageDefServiceLocal extends java.io.Serializable  {
    public static final String EDIT_STUDY_PAGE = "EditStudyPage";
    public static final String ADD_SITE_PAGE = "AddSitePage";
    public static final String VIEW_STUDY_PAGE = "StudyPage";
    public static final String SUBSETTING_PAGE = "SubsettingPage";
    public static final String EXPLOREDATA_PAGE = "ExploreDataPage";
    public static final String UNAUTHORIZED_PAGE = "UnauthorizedPage";
    public static final String STUDYLOCKED_PAGE = "StudyLockedPage";
    public static final String CONTRIBUTOR_REQUEST = "ContributorRequestPage";
    public static final String CREATOR_REQUEST = "CreatorRequestPage";
    public static final String ERROR_PAGE = "ErrorPage";
    public static final String LOGOUT_PAGE = "LogoutPage";
    public static final String LOGIN_PAGE = "LoginPage";
    public static final String ADD_ACCOUNT_PAGE = "AddAccountPage";
    public static final String EDIT_ACCOUNT_PAGE = "EditAccountPage";
    public static final String ADD_FILES_PAGE = "AddFilesPage";
    public static final String EDIT_FILES_PAGE ="EditStudyFilesPage";
    public static final String STUDY_PERMISSIONS_PAGE = "StudyPermissionsPage";
    public static final String DELETE_STUDY_PAGE = "DeleteStudyPage";
    public static final String CONTRIBUTOR_REQUEST_ACCOUNT_PAGE = "ContributorRequestAccountPage";
    public static final String CONTRIBUTOR_REQUEST_INFO_PAGE = "ContributorRequestInfoPage";
    public static final String CONTRIBUTOR_REQUEST_SUCCESS_PAGE = "ContributorRequestSuccessPage";
    public static final String CONTRIBUTOR_REQUEST_PAGE = "ContributorRequestPage";
    public static final String CREATOR_REQUEST_ACCOUNT_PAGE = "CreatorRequestAccountPage";
    public static final String CREATOR_REQUEST_INFO_PAGE = "CreatorRequestInfoPage";
    public static final String CREATOR_REQUEST_SUCCESS_PAGE = "CreatorRequestSuccessPage";
    public static final String CREATOR_REQUEST_PAGE = "CreatorRequestPage";
    public static final String FILE_REQUEST_ACCOUNT_PAGE = "FileRequestAccountPage";
    public static final String FILE_REQUEST_SUCCESS_PAGE = "FileRequestSuccessPage";
    public static final String FILE_REQUEST_PAGE = "FileRequestPage";
    public static final String EDIT_VARIABLE_PAGE = "EditVariablePage";
    public static final String FORGOT_PASSWORD_PAGE = "ForgotPasswordPage";
    public static final String TERMS_OF_USE_PAGE = "TermsOfUsePage";
    public static final String ACCOUNT_TERMS_OF_USE_PAGE = "AccountTermsOfUsePage";
    public static final String DV_OPTIONS_PAGE = "OptionsPage";
    public static final String NETWORK_OPTIONS_PAGE = "NetworkOptionsPage";
    public static final String MANAGE_STUDIES_PAGE = "ManageStudiesPage";
    public static final String ACCOUNT_OPTIONS_PAGE = "AccountOptionsPage";
    public static final String ACCOUNT_PAGE = "AccountPage";
    public static final String STUDY_VERSION_DIFFERENCES_PAGE = "StudyVersionDifferencesPage";
    public static final String MANIFEST_PAGE = "ManifestPage";
    public static final String SETUP_DATA_EXPLORATION_PAGE = "SetUpDataExplorationPage";
    public static final String DEACCESSION_STUDY_PAGE = "DeaccessionStudyPage";
    
    Role findById(Long id);

    PageDef findByName(String name);

    PageDef findByPath(String path);
    
}
