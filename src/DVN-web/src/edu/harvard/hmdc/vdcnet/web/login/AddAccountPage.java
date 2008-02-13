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
package edu.harvard.hmdc.vdcnet.web.login;

import javax.faces.component.html.HtmlSelectOneMenu;
import com.sun.jsfcl.data.DefaultSelectItemsArray;
import edu.harvard.hmdc.vdcnet.admin.EditUserService;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.util.CharacterValidator;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.component.html.HtmlSelectManyCheckbox;
import javax.faces.context.FacesContext;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class AddAccountPage extends VDCBaseBean implements java.io.Serializable  {
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
        
    }
    
    
    
    
    
    private HtmlSelectOneMenu dropdown1 = new HtmlSelectOneMenu();
    
    public HtmlSelectOneMenu getDropdown1() {
        return dropdown1;
    }
    
    public void setDropdown1(HtmlSelectOneMenu hsom) {
        this.dropdown1 = hsom;
    }
    
    private DefaultSelectItemsArray dropdown1DefaultItems = new DefaultSelectItemsArray();
    
    public DefaultSelectItemsArray getDropdown1DefaultItems() {
        return dropdown1DefaultItems;
    }
    
    
    
    private HtmlSelectManyCheckbox checkboxList1 = new HtmlSelectManyCheckbox();
    
    public HtmlSelectManyCheckbox getCheckboxList1() {
        return checkboxList1;
    }
    
    public void setCheckboxList1(HtmlSelectManyCheckbox hsmc) {
        this.checkboxList1 = hsmc;
    }
    
    private DefaultSelectItemsArray checkboxList1DefaultItems = new DefaultSelectItemsArray();
    
    public DefaultSelectItemsArray getCheckboxList1DefaultItems() {
        return checkboxList1DefaultItems;
    }
    
    public void setCheckboxList1DefaultItems(DefaultSelectItemsArray dsia) {
        this.checkboxList1DefaultItems = dsia;
    }
    
    private UISelectItems checkboxList1SelectItems = new UISelectItems();
    
    public UISelectItems getCheckboxList1SelectItems() {
        return checkboxList1SelectItems;
    }
    
    public void setCheckboxList1SelectItems(UISelectItems uisi) {
        this.checkboxList1SelectItems = uisi;
    }
    
    private UISelectItems dropdown1SelectItems = new UISelectItems();
    
    public UISelectItems getDropdown1SelectItems() {
        return dropdown1SelectItems;
    }
    
    public void setDropdown1SelectItems(UISelectItems uisi) {
        this.dropdown1SelectItems = uisi;
    }
    
    
    // </editor-fold>
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
            if (this.getRequestParam("studyId")!=null) {
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
     * <p>Callback method that is called after rendering is completed for
     * this request, if <code>init()</code> was called (regardless of whether
     * or not this was the page that was actually rendered).  Customize this
     * method to release resources acquired in the <code>init()</code>,
     * <code>preprocess()</code>, or <code>prerender()</code> methods (or
     * acquired during execution of an event handler).</p>
     */
    public void destroy() {
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
        getVDCSessionBean().setUserService(null);
        return "home";
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

