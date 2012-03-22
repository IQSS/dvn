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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.login;

import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.util.PropertyUtil;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.web.StudyListing;
import edu.harvard.iq.dvn.core.web.common.LoginBean;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.common.VDCSessionBean;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Ellen Kraffmiller
 */
@Named("LoginWorkflowBean")
@SessionScoped
public class LoginWorkflowBean extends VDCBaseBean implements java.io.Serializable  {

    @EJB
    UserServiceLocal userService;
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    private String workflowType;
    private VDCUser user;
    private Long studyId;
    public static String WORKFLOW_TYPE_CONTRIBUTOR = "contributor";
    public static String WORKFLOW_TYPE_CREATOR = "creator";
    public static String WORKFLOW_TYPE_FILE_ACCESS = "fileAccess";
    public static String WORKFLOW_TYPE_COMMENTS = "comments";

    /** Creates a new instance of LoginWorkflowBean */
    public LoginWorkflowBean() {
    }

    public String beginLoginWorkflow() {
        clearWorkflowState();
        return "/login/LoginPage?faces-redirect=true";
    }
    
    public String beginFileAccessWorkflow(Long studyId) {
        clearWorkflowState();
        workflowType="fileAccess"; 
        this.studyId=studyId;
          String nextPage =  null;
          LoginBean loginBean = this.getVDCSessionBean().getLoginBean();
        if (loginBean == null) {
            nextPage = "/login/FileRequestAccountPage?faces-redirect=true";
        } else {
            nextPage = "/login/FileRequestPage?faces-redirect=true&studyId="+studyId; 
        }
  
        return nextPage;
    }

    public String beginCommentsWorkflow(Long studyId) {
        clearWorkflowState();
        workflowType = WORKFLOW_TYPE_COMMENTS;
        this.studyId=studyId;
        String nextPage = "/login/AddAccountPage?faces-redirect=true";
        return nextPage;
    }
    
    
    public String beginCreatorWorkflow() {
        clearWorkflowState();
        workflowType = WORKFLOW_TYPE_CREATOR;
        String nextPage = null;
        LoginBean loginBean = this.getVDCSessionBean().getLoginBean();
        if (loginBean != null) {
            user = loginBean.getUser();
            grantWorkflowPermission();
            loginBean.setUser(user); // user may have been modified by call to grantWorkflowPermission
            nextPage = "/site/AddSitePage?faces-redirect=true";
        } else {
         
            nextPage = "/login/AddAccountPage?faces-redirect=true";
        }
        return nextPage;
    }
    
    public String beginContributorWorkflow() {
        clearWorkflowState();
        workflowType = this.WORKFLOW_TYPE_CONTRIBUTOR;
        String nextPage = null;
        LoginBean loginBean = this.getVDCSessionBean().getLoginBean();
        if (loginBean != null) {
            user = loginBean.getUser();
            grantWorkflowPermission();
            loginBean.setUser(user); // user may have been modified by call to grantWorkflowPermission
            nextPage = "/login/ContributorRequestSuccessPage?faces-redirect=true";
        } else {   
            nextPage = "/login/AddAccountPage?faces-redirect=true";
        }
        return nextPage;
    }
      

    public String beginLoginCreatorWorkflow() {
        clearWorkflowState();
        workflowType =WORKFLOW_TYPE_CREATOR;
        String nextPage = "/login/LoginPage?faces-redirect=true";
        return nextPage;

    }
    
       public String beginLoginContributorWorkflow() {
        clearWorkflowState();
        workflowType =   WORKFLOW_TYPE_CONTRIBUTOR;
 
        String nextPage = "/login/LoginPage?faces-redirect=true";
        return nextPage;

    }


    public String processLogin(VDCUser user, Long studyId) {
        this.user = user;
        if (studyId != null) {
            this.studyId = studyId;
        }
        String nextPage = null;
        if (user.isAgreedTermsOfUse() || !vdcNetworkService.find().isTermsOfUseEnabled()) {
            nextPage = updateSessionAndRedirect();
        } else {
            
            nextPage = "/login/AccountTermsOfUsePage?faces-redirect=true";
        }
        return nextPage;
    }

  public String processAddAccount(VDCUser newUser) {
        user = newUser;
        String nextPage = null;
        if (workflowType == null) {
            nextPage = "/login/AccountPage?faces-redirect=true&userId=" + user.getId() + getNavigationVDCSuffix();
        } else if (vdcNetworkService.find().isTermsOfUseEnabled()) {
            nextPage = "/login/AccountTermsOfUsePage?faces-redirect=true";
        } else {
            if (workflowType.equals(WORKFLOW_TYPE_CONTRIBUTOR)) {
                //nextPage = "contributorSuccess";
                nextPage = "/login/ContributorRequestSuccessPage?faces-redirect=true" + getNavigationVDCSuffix();
            } else if (workflowType.equals(WORKFLOW_TYPE_CREATOR)) {
                nextPage = "/site/AddSitePage?faces-redirect=true";
            } else if (workflowType.equals(WORKFLOW_TYPE_FILE_ACCESS)) {
               nextPage = "/login/FileRequestPage?faces-redirect=true&studyId="+studyId + getNavigationVDCSuffix();
            } else if (workflowType.equals(WORKFLOW_TYPE_COMMENTS)) {
               nextPage = "/study/StudyPage?faces-redirect=true&studyId="+studyId + getNavigationVDCSuffix();
            }
            updateSessionForLogin();
        }
        return nextPage;
    }

    public String processTermsOfUse(boolean termsAccepted) {
        String forward = "/HomePage?faces-redirect=true";
        
        if (user != null) {
            if (termsAccepted) {
                userService.setAgreedTermsOfUse(user.getId(), termsAccepted);
                user.setAgreedTermsOfUse(termsAccepted);  // update detached object because it will be added to the loginBean
                forward = updateSessionAndRedirect();
            }
        }

        return forward;

    }

    private String updateSessionAndRedirect() {
        updateSessionForLogin();
        return setLoginRedirect();
     
    }
    
   
    /**
     *  Create loginBean and add it to the Session, do other session-related updates.
     * @param session
     * @param sessionMap
     * @param vdcSessionBean
     * @param user
     */
    
    private void updateSessionForLogin() {
        //first remove any existing ipUserGroup info from the session
        ExternalContext externalContext = getExternalContext();
        Map sessionMap = getSessionMap();
        VDCSessionBean vdcSessionBean = getVDCSessionBean();
        HttpSession session = (HttpSession) externalContext.getSession(true);
        if (vdcSessionBean.getIpUserGroup() != null) {
            session.removeAttribute("ipUserGroup");
            session.removeAttribute("isIpGroupChecked");
        }
        grantWorkflowPermission();
        LoginBean loginBean = new LoginBean();
        loginBean.setUser(user);
        vdcSessionBean.setLoginBean(loginBean);

        // copy all terms of use from session TermsOfUseMap
        loginBean.getTermsfUseMap().putAll(vdcSessionBean.getTermsfUseMap());
        // then clear the sessions version
        vdcSessionBean.getTermsfUseMap().clear();
        // clear the studylistings from prelogin
        StudyListing.clearStudyListingMap(sessionMap);
     }
    
     public void clearWorkflowState() {
         workflowType=null;
         user=null;
         studyId=null;
     }
    
     private void grantWorkflowPermission() {
        if (workflowType!=null) {
            if (workflowType.equals(WORKFLOW_TYPE_CREATOR)) {
                  userService.makeCreator(user.getId());
            } 
            else if (workflowType.equals(WORKFLOW_TYPE_CONTRIBUTOR)) {
                // this workflow no longer makes the contributor immediately (it is used to create an account);
                // instead the user will become a contributor when they make an actual contribution
                  
            } else if  (workflowType.equals(WORKFLOW_TYPE_FILE_ACCESS)) {
                // give study file permission
            }
            // Update detached user object with updated user from database
            user = userService.find(user.getId());
        
        }
    }

    private String setLoginRedirect() {
        Map sessionMap = getSessionMap();
        VDC currentVDC = getVDCRequestBean().getCurrentVDC();
        
        String redirectString = "";
         
        if (WORKFLOW_TYPE_CONTRIBUTOR.equals(workflowType)) {
            redirectString = "/login/ContributorRequestSuccessPage.xhtml";
        } else if (WORKFLOW_TYPE_CREATOR.equals(workflowType)) {
            redirectString = "/site/AddSitePage.xhtml";
        } else if (WORKFLOW_TYPE_FILE_ACCESS.equals(workflowType)) {
            if (currentVDC != null) {
                redirectString = "/login/FileRequestPage.xhtml?studyId=" + studyId;
            } else {
                redirectString = "/login/FileRequestPage.xhtml";
            }
        } else {
            if (sessionMap.get("ORIGINAL_URL") != null) {
                String originalURL = (String) sessionMap.get("ORIGINAL_URL");
                redirectString = originalURL.substring(originalURL.indexOf("/faces/") + 6);
                sessionMap.remove("ORIGINAL_URL");
            } else {
                //  HttpServletRequest request = this.getExternalContext().getRequestContextPath()
                if (currentVDC != null) {
                    redirectString = "/StudyListingPage.xhtml";
                } else {
                    redirectString = "/HomePage.xhtml";
                }
            }
        }
        
        boolean hasParams = redirectString.indexOf("?") != -1;
        redirectString += (hasParams ? "&" : "?") + "faces-redirect=true";
        redirectString += getVDCRequestBean().getCurrentVDC() != null ? "&vdcId=" + getVDCRequestBean().getCurrentVDCId() : "";
        return redirectString;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public VDCUser getUser() {
        return user;
    }

    public void setUser(VDCUser user) {
        this.user = user;
    }
    
    public String getWorkflowType() {
        return workflowType;
    }
    
    /**
     * Used in the Creator Workflow Success page - for url of dataverse home page.
     * @return hostUrl (based on inet Address)
     */
    public String getHostUrl() {
        return PropertyUtil.getHostUrl();
    }
    
    public boolean isContributorWorkflow() {
        return workflowType!=null && workflowType.equals(WORKFLOW_TYPE_CONTRIBUTOR);
    }
    public boolean isCreatorWorkflow() {
        return workflowType!=null && workflowType.equals(WORKFLOW_TYPE_CREATOR);
    }
    public boolean isFileAccessWorkflow() {
        return workflowType!=null && workflowType.equals(WORKFLOW_TYPE_FILE_ACCESS);
    }
    public boolean isPlainWorkflow() {
        return workflowType==null;
    }
   
}
