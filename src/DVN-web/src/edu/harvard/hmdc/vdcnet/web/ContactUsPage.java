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

package edu.harvard.hmdc.vdcnet.web;

import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlCommandButton;
import com.sun.jsfcl.data.DefaultSelectItemsArray;
import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.util.ExceptionMessageWriter;
import java.util.Iterator;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

/*
 * ContactUsPage.java
 *
 * Created on October 18, 2006, 4:24 PM
 */

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class ContactUsPage extends VDCBaseBean implements java.io.Serializable {
    @EJB MailServiceLocal mailService;
    
    private String ERROR_MESSAGE   = new String("An Error Occurred.");
    private String SUCCESS_MESSAGE = new String("An e-mail has been sent successfully!");
    private String EMAIL_ERROR_MESSAGE = new String("An error occurred. The message was not sent.");
    
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    
    
    // </editor-fold>


    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public ContactUsPage() {
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
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
        listSubjectDefaults.setItems(new String[] {"-  Select a subject -", "Issue accessing a study or studies", "Issue downloading a file or files", "Issue logging in or creating an account", "Issues with Analysis and Subsetting", "Curator/Administration Issues", "Need more information about a topic", "Other question or issue"});
        this.setFullName((getVDCSessionBean().getLoginBean() != null) ? getVDCSessionBean().getLoginBean().getUser().getFirstName() + " " + getVDCSessionBean().getLoginBean().getUser().getLastName() : "");
        this.setEmailAddress((getVDCSessionBean().getLoginBean() != null) ? getVDCSessionBean().getLoginBean().getUser().getEmail() : "");
        this.setEmailBody((this.getEmailBody() != null) ? this.getEmailBody() : "");
        this.setSelectedSubject((this.getSelectedSubject() != null) ? this.getSelectedSubject() : "");
        success = false;
        exception = false;
    }

    private HtmlCommandButton btnSend = new HtmlCommandButton();

    public HtmlCommandButton getBtnSend() {
        return btnSend;
    }

    public void setBtnSend(HtmlCommandButton hcb) {
        this.btnSend = hcb;
    }

    private HtmlCommandButton btnCancel = new HtmlCommandButton();

    public HtmlCommandButton getButtonCancel() {
        return btnCancel;
    }

    public void setBtnCancel(HtmlCommandButton hcb) {
        this.btnCancel = hcb;
    }
    
    private String fullName = "";
    
    public String getFullName() {
        return this.fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    private String emailAddress;
    
    public String getEmailAddress() {
        return this.emailAddress;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    private String selectedSubject;
    
    public String getSelectedSubject () {
        return selectedSubject;
    }
    
    public void setSelectedSubject(String selectedSubject) {
        this.selectedSubject = selectedSubject;
    }
    
    private HtmlSelectOneMenu listSubjects = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getListSubjects() {
        return listSubjects;
    }

    public void setListSubjects(HtmlSelectOneMenu hsom) {
        this.listSubjects = hsom;
    }
    
    //select list identifier
    private UISelectItems listSubjectItems = new UISelectItems();

    public UISelectItems getListSubjectItems() {
        return listSubjectItems;
    }

    public void setListSubjectItems(UISelectItems uisi) {
        this.listSubjectItems = uisi;
    }
    
    //default select list items
    private DefaultSelectItemsArray listSubjectDefaults = new DefaultSelectItemsArray();

    public DefaultSelectItemsArray getListSubjectDefaults() {
        return listSubjectDefaults;
    }

    public void setListSubjectDefaults(DefaultSelectItemsArray dsia) {
        this.listSubjectDefaults = dsia;
    }
    
    //email body
    private String emailBody;

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }
    
     /**
     * Holds value of property success.
     */
    private boolean success;

    /**
     * Getter for property success.
     * @return Value of property success.
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * Setter for property success.
     * @param success New value of property success.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    /**
     * Holds value of property success.
     */
    private boolean exception;

    /**
     * Getter for property success.
     * @return Value of property success.
     */
    public boolean isException() {
        return this.exception;
    }

    /**
     * Setter for property success.
     * @param success New value of property success.
     */
    public void setException(boolean exception) {
        this.exception = exception;
    }
    
    //ACTION METHODS
    public String send_action() {
        String msg  = SUCCESS_MESSAGE;
        success = true;
        try {
            String fromAddress = "\"" + fullName + "\"<" + emailAddress.trim() + ">";
            mailService.sendMail(fromAddress, (getVDCRequestBean().getCurrentVDCId() == null) ? getVDCRequestBean().getVdcNetwork().getContactEmail() : getVDCRequestBean().getCurrentVDC().getContactEmail(), (getVDCRequestBean().getCurrentVDCId()==null) ? getVDCRequestBean().getVdcNetwork().getName()  + " Dataverse Network: " + selectedSubject.trim() : getVDCRequestBean().getCurrentVDC().getName() + " dataverse: " + selectedSubject.trim(), emailBody.trim());
        } catch (Exception e) {
            success     = false;
            exception   = true;
            msg = EMAIL_ERROR_MESSAGE;
            ExceptionMessageWriter.logException(e);
        } finally {
            ExceptionMessageWriter.addGlobalMessage(msg);
            if (success) return "success"; else return "result";
        }
    }
    
    public String cancel_action(){
        if (getVDCRequestBean().getCurrentVDCId() == null)
            return "cancelNetwork";
        else
            return "cancelVDC";
    }
      
        /* validateSubject
     *
     **<p> Utility method to validate the Subject
     * selected.</p>
     *
     * @author Wendy Bossons
     */
    public void validateSubject(FacesContext context, 
                          UIComponent toValidate,
                          Object value) {
        String subject = (String) value;
        if (subject.indexOf("Select a subject") != -1) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Please select a subject.");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }
    
}

