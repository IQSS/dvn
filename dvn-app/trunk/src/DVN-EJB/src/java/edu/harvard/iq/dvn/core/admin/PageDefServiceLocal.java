/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
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
    Role findById(Long id);

    PageDef findByName(String name);

    PageDef findByPath(String path);
    
}
