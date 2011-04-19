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
 */
package edu.harvard.iq.dvn.core.web.login;

import edu.harvard.iq.dvn.core.admin.EditUserService;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.web.util.CharacterValidator;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.web.common.StatusMessage;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import com.icesoft.faces.component.ext.HtmlInputHidden;
import com.icesoft.faces.component.ext.HtmlInputSecret;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class AddAccountPage extends VDCBaseBean implements java.io.Serializable  {
  
    @EJB EditUserService editUserService;
    @EJB UserServiceLocal userService;
    @EJB MailServiceLocal mailService;
    @EJB StudyServiceLocal studyService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    HtmlInputSecret inputPassword;


    public HtmlInputSecret getInputPassword() {
        return inputPassword;
    }

    public void setInputPassword(HtmlInputSecret inputPassword) {
        this.inputPassword = inputPassword;
    }
     
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public AddAccountPage() {
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
        if ( isFromPage("AddAccountPage") ) {
            editUserService = (EditUserService) sessionGet(editUserService.getClass().getName());
            user = editUserService.getUser();
            
        } else {
            editUserService.newUser();
            sessionPut( editUserService.getClass().getName(), editUserService);
            //sessionPut( (studyService.getClass().getName() + "."  + studyId.toString()), studyService);
            user = editUserService.getUser();
            HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
            if (request.getAttribute("studyId") != null) {
                studyId = new Long(request.getAttribute("studyId").toString());
                editUserService.setRequestStudyId(studyId);
            } else if (this.getRequestParam("studyId") != null) {
                studyId = new Long(Long.parseLong(getRequestParam("studyId")));
                editUserService.setRequestStudyId(studyId);
            }
            
        }
        studyId = editUserService.getRequestStudyId();
        if (studyId!=null) {
            study = studyService.getStudy(studyId);
        }
        
    }
   
    
    /**
     * Holds value of property user.
     */
    private VDCUser user;
    
    /**
     * Getter for property user.
     * @return Value of property user.
     */
    public VDCUser getUser() {
        return this.user;
    }
    
    /**
     * Setter for property user.
     * @param user New value of property user.
     */
    public void setUser(VDCUser user) {
        this.user = user;
    }
    
    public EditUserService getEditUserService() {
        return editUserService;
    }
    
    public void validateUserName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        CharacterValidator charactervalidator = new CharacterValidator();
        charactervalidator.validate(context, toValidate, value);
        String userName = (String) value;
        
        boolean userNameFound = false;
        VDCUser user = userService.findByUserName(userName);
        if (user!=null) {
            userNameFound=true;
        }
        
        if (userNameFound) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("This Username is already taken.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
    
    public String createAccount() {
        String forwardPage=null;
        Long contributorRequestVdcId = null;
        String workflowValue=null;
        user.setActive(true);
        editUserService.save();
        if (StringUtil.isEmpty(workflowValue)) {
            StatusMessage msg = new StatusMessage();
            msg.setMessageText("User account created successfully.");
            msg.setStyleClass("successMessage");
            getRequestMap().put("statusMessage",msg);
            forwardPage="viewAccount";
        } 
        LoginWorkflowBean loginWorkflowBean = (LoginWorkflowBean)this.getBean("LoginWorkflowBean");       
        return loginWorkflowBean.processAddAccount(user);
      
    }
    
    public String cancel() {
        editUserService.cancel();
 
        // if user is logged in return to the appropriate options page
        // if not logged in, to the appropriate home page
        if (getVDCSessionBean().getLoginBean() != null) {
            if (getVDCRequestBean().getCurrentVDC() != null) {
                return "cancelVDC";
            } else {
                return "cancelNetwork";
            }
        } else {
            return getVDCRequestBean().home();
        }
    }
    
    /**
     * Holds value of property workflow.
     */
    private String workflow;
    
    /**
     * Getter for property contributorRequest.
     * @return Value of property contributorRequest.
     */
    public String getWorkflow() {
        return this.workflow;
    }
    
    /**
     * Setter for property contributorRequest.
     * @param contributorRequest New value of property contributorRequest.
     */
    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }
    
    /**
     * Holds value of property studyRequest.
     */
    private boolean studyRequest;
    
    /**
     * Getter for property studyRequest.
     * @return Value of property studyRequest.
     */
    public boolean isStudyRequest() {
        return this.studyRequest;
    }
    
    /**
     * Setter for property studyRequest.
     * @param studyRequest New value of property studyRequest.
     */
    public void setStudyRequest(boolean studyRequest) {
        this.studyRequest = studyRequest;
    }
    
    /**
     * Holds value of property studyId.
     */
    private Long studyId;
    
    /**
     * Getter for property studyId.
     * @return Value of property studyId.
     */
    public Long getStudyId() {
        return this.studyId;
    }
    
    /**
     * Setter for property studyId.
     * @param studyId New value of property studyId.
     */
    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }
    
    /**
     * Holds value of property accessStudy.
     */
    private boolean accessStudy;
    
    /**
     * Getter for property accessStudy.
     * @return Value of property accessStudy.
     */
    public boolean isAccessStudy() {
        return this.accessStudy;
    }
    
    /**
     * Setter for property accessStudy.
     * @param accessStudy New value of property accessStudy.
     */
    public void setAccessStudy(boolean accessStudy) {
        this.accessStudy = accessStudy;
    }
    
    /**
     * Holds value of property study.
     */
    private Study study;
    
    /**
     * Getter for property study.
     * @return Value of property study.
     */
    public Study getStudy() {
        return this.study;
    }
    
    /**
     * Setter for property study.
     * @param study New value of property study.
     */
    public void setStudy(Study study) {
        this.study = study;
    }
    
    /**
     * Holds value of property hiddenWorkflow.
     */
    private HtmlInputHidden hiddenWorkflow;
    
    /**
     * Getter for property hiddenContributorRequest.
     * @return Value of property hiddenContributorRequest.
     */
    public HtmlInputHidden getHiddenWorkflow() {
        return this.hiddenWorkflow;
    }
    
    /**
     * Setter for property hiddenContributorRequest.
     * @param hiddenContributorRequest New value of property hiddenContributorRequest.
     */
    public void setHiddenWorkflow(HtmlInputHidden hiddenWorkflow) {
        this.hiddenWorkflow = hiddenWorkflow;
    }
    
    
    public void validatePassword(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String retypedPassword = (String) value;
        String errorMessage = null;
        
        // check invalid characters
        if (!inputPassword.getLocalValue().equals(retypedPassword)  ) {
            errorMessage = "Passwords do not match.";
        }
        
        
        
        if (errorMessage != null) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage(errorMessage);
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
}

