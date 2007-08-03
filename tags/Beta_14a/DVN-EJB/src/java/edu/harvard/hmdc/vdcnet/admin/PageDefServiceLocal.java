/*
 * PageDefServiceLocal.java
 *
 * Created on November 7, 2006, 4:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

/**
 *
 * @author Ellen Kraffmiller
 */
public interface PageDefServiceLocal {
    public static final String EDIT_STUDY_PAGE = "EditStudyPage";
    public static final String ADD_SITE_PAGE = "AddSitePage";
    public static final String VIEW_STUDY_PAGE = "StudyPage";
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
    public static final String CONTRIBUTOR_REQUEST_SUCCESS_PAGE = "ContributorRequestSuccessPage";
    public static final String CONTRIBUTOR_REQUEST_PAGE = "ContributorRequestPage";
    public static final String CREATOR_REQUEST_ACCOUNT_PAGE = "CreatorRequestAccountPage";
    public static final String CREATOR_REQUEST_SUCCESS_PAGE = "CreatorRequestSuccessPage";
    public static final String CREATOR_REQUEST_PAGE = "CreatorRequestPage";
     
    Role findById(Long id);

    PageDef findByName(String name);

    PageDef findByPath(String path);
    
}
