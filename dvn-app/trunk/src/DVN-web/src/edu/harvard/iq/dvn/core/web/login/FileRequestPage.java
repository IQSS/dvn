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
 * AddAccountPage.java
 *
 * Created on October 4, 2006, 1:04 PM
 *
 */
package edu.harvard.iq.dvn.core.web.login;

import edu.harvard.iq.dvn.core.admin.EditUserService;
import edu.harvard.iq.dvn.core.admin.RoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.StudyAccessRequestServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlInputHidden;
import javax.inject.Named;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@ViewScoped
@Named("FileRequestPage")
public class FileRequestPage extends VDCBaseBean implements java.io.Serializable  {

    @EJB
    StudyAccessRequestServiceLocal studyRequestService;
    @EJB
    EditUserService editService;
    @EJB
    RoleServiceLocal roleService;
    @EJB
    StudyServiceLocal studyService;
    @EJB
    MailServiceLocal mailService;
    @EJB
    UserServiceLocal userService;
    
    Long studyId;

    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public FileRequestPage() {
    }

    /**
     * <p>Callback method that is called whenever a page is navigated to,
     * either directly via a URL, or indirectly via page navigation.
     * Customize this method to acquire resources that will be needed
     * for event handlers and lifecycle methods, whether or not this
     * page is performing post back processing.</p>
     *
     * <p>Note that, if the current request is a postback, the property
     * values of the components do <strong>not</strong> represent any
     * values submitted with this request.  Instead, they represent the
     * property values that were saved for this view when it was rendered.</p>
     */
    public void init() {
        super.init();
        if (studyId!=null) {
            LoginWorkflowBean lwf = (LoginWorkflowBean) getBean("LoginWorkflowBean");       
            lwf.beginFileAccessWorkflow(studyId);
        }
   
   
    }

    /**
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
    }

    /**
     * <p>Callback method that is called just before rendering takes place.
     * This method will <strong>only</strong> be called for the page that
     * will actually be rendered (and not, for example, on a page that
     * handled a postback and then navigated to a different page).  Customize
     * this method to allocate resources that will be required for rendering
     * this page.</p>
     */
    public void prerender() {
    }
  
    /**
     * Getter for property alreadyRequested.
     * @return Value of property alreadyRequested.
     */
    public boolean isAlreadyRequested() {
        Long requestStudyId=null;
        LoginWorkflowBean lwf = (LoginWorkflowBean) getBean("LoginWorkflowBean");
      
        boolean alreadyRequested=false;
     
        VDCUser user = this.getVDCSessionBean().getLoginBean().getUser();
        if (studyRequestService.findByUserStudy(user.getId(), getRequestStudyId()) != null) {
            alreadyRequested = true;
        }
        return alreadyRequested;
    }

    private Long getRequestStudyId() {
        LoginWorkflowBean lwf = (LoginWorkflowBean) getBean("LoginWorkflowBean");
        if (studyId!=null) {
            return studyId;
        } else {
            return lwf.getStudyId();
        }
    }
    public String generateRequest() {

        LoginWorkflowBean lwf = (LoginWorkflowBean) getBean("LoginWorkflowBean");
        VDCUser user = this.getVDCSessionBean().getLoginBean().getUser();
        Study study = studyService.getStudy(getRequestStudyId());
       
        studyRequestService.create(user.getId(), study.getId());
        // Notify Admin of request
        
        mailService.sendFileAccessRequestNotification(study.getOwner().getContactEmail(), user.getUserName(), study.getReleasedVersion().getMetadata().getTitle(), study.getGlobalId());

        // Send confirmation to user
 
        mailService.sendFileAccessRequestConfirmation(user.getEmail(), study.getReleasedVersion().getMetadata().getTitle(), study.getGlobalId());
     
        getVDCRenderBean().getFlash().put("successMessage", "Thanks for your interest in this study. You will be notified as soon as your request is approved.");
        return "/study/StudyPage.xhtml?faces-redirect=true&studyId=" + getStudyId() + "&tab=files" + getNavigationVDCSuffix();

    }
    
    public String cancel() {
        return "/study/StudyPage.xhtml?faces-redirect=true&studyId=" + getStudyId() + "&tab=files" + getNavigationVDCSuffix();        
    }
    
    
    /**
     * Holds value of property fileRequest.
     */
    private boolean fileRequest;

    /**
     * Getter for property contributorRequest.
     * @return Value of property contributorRequest.
     */
    public boolean isFileRequest() {
        return this.fileRequest;
    }

    /**
     * Setter for property contributorRequest.
     * @param contributorRequest New value of property contributorRequest.
     */
    public void setFileRequest(boolean fileRequest) {
        this.fileRequest = fileRequest;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }
    
}

