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
package edu.harvard.iq.dvn.core.web;

import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.logging.Logger;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.model.SelectItem;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.web.util.ExceptionMessageWriter;
import edu.harvard.iq.dvn.core.vdc.Captcha;
import edu.harvard.iq.dvn.core.vdc.CaptchaServiceLocal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

/*
 * ContactUsPage.java
 *
 * Created on October 18, 2006, 4:24 PM
 */
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import net.tanesha.recaptcha.http.SimpleHttpLoader;


@ViewScoped
@Named("ContactUsPage")
public class ContactUsPage extends VDCBaseBean implements java.io.Serializable {
    @EJB MailServiceLocal mailService;
    @EJB CaptchaServiceLocal captchService;
    
    private String SUCCESS_MESSAGE = new String("An e-mail has been sent successfully!");
    private String EMAIL_ERROR_MESSAGE = new String("An error occurred. The message was not sent.");
    private Captcha c;
    private ReCaptchaImpl r;
    private SimpleHttpLoader l;
    private boolean hasValidationErrors = false;
    
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    
    
    // </editor-fold>


    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public ContactUsPage() {
    }


    public void init() {
        super.init();
         this.setFullName((getVDCSessionBean().getLoginBean() != null) ? getVDCSessionBean().getLoginBean().getUser().getFirstName() + " " + getVDCSessionBean().getLoginBean().getUser().getLastName() : "");
        this.setEmailAddress((getVDCSessionBean().getLoginBean() != null) ? getVDCSessionBean().getLoginBean().getUser().getEmail() : "");
        this.setEmailBody((this.getEmailBody() != null) ? this.getEmailBody() : "");
        this.setSelectedSubject((this.getSelectedSubject() != null) ? this.getSelectedSubject() : "");
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
    
  
    public ArrayList<SelectItem> getListSubjectItems() {
        ArrayList<SelectItem> arr = new ArrayList<SelectItem>();
        arr.add(new SelectItem("-  Select a subject -"));
        arr.add(new SelectItem("Issue accessing a study or studies"));
        arr.add(new SelectItem("Issue downloading a file or files"));
        arr.add(new SelectItem("Issue logging in or creating an account"));
        arr.add(new SelectItem("Issues with Analysis and Subsetting"));
        arr.add(new SelectItem("Curator/Administration Issues"));
        arr.add(new SelectItem("Issues with Analysis and Subsetting"));
        arr.add(new SelectItem("Need more information about a topic"));
        arr.add(new SelectItem("Other question or issue"));
        return arr;

    }

   
   
    
    //email body
    private String emailBody;

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }



    private String emailRecipient;
    
    public String getEmailRecipient(){
        if (emailRecipient == null){
            setEmailRecipient(getVDCRequestBean().getCurrentVDC().getContactEmail());
        }
        return emailRecipient;
    }


    public void setEmailRecipient(String inStr){
        this.emailRecipient = inStr;
    }

    private List<SelectItem> emailRecipientOptions = null;

    public List getEmailRecipientOptions() {
        if (this.emailRecipientOptions == null) {
            emailRecipientOptions = new ArrayList();
                String networkOption       = getVDCRequestBean().getVdcNetwork().getContactEmail();
                String dataverseOption     = getVDCRequestBean().getCurrentVDC().getContactEmail();
                String networkDescription  =  getVDCRequestBean().getVdcNetwork().getName() + " Network Administrator";
                String dataverseDescription = getVDCRequestBean().getCurrentVDC().getName() + " Dataverse Administrator";
                emailRecipientOptions.add(new SelectItem(dataverseOption, dataverseDescription));
                emailRecipientOptions.add(new SelectItem(networkOption, networkDescription));          
        }
        return emailRecipientOptions;
    }

    //ACTION METHODS

    private String getToEmailAddress(){
        if(getVDCRequestBean().getCurrentVDCId() == null ){
            return getVDCRequestBean().getVdcNetwork().getContactEmail();
        }
        else
        {
            return emailRecipient;
        }
    }

    public String send_action() {
        String msg  = SUCCESS_MESSAGE;
        boolean success = true;
        try {
            String fromAddress = "\"" + fullName + "\"<" + emailAddress.trim() + ">";
            mailService.sendMail(fromAddress, getToEmailAddress(), (getVDCRequestBean().getCurrentVDCId()==null) ? getVDCRequestBean().getVdcNetwork().getName()  + " Dataverse Network: " + selectedSubject.trim() : getVDCRequestBean().getCurrentVDC().getName() + " dataverse: " + selectedSubject.trim(), emailBody.trim());
  
            getVDCRenderBean().getFlash().put("successMessage",SUCCESS_MESSAGE);
            getVDCRenderBean().getFlash().put("fullName",fullName);
            getVDCRenderBean().getFlash().put("emailAddress",emailAddress);
            getVDCRenderBean().getFlash().put("selectedSubject",selectedSubject);
            getVDCRenderBean().getFlash().put("emailBody",emailBody);
            return "/ContactUsConfirmPage.xhtml?faces-redirect=true";             
            
            
        } catch (Exception e) {
            getVDCRenderBean().getFlash().put("warningMessage",EMAIL_ERROR_MESSAGE);
            return "";

        }
        
    }
    

      

    public void validateSubject(FacesContext context, 
                          UIComponent toValidate,
                          Object value) {
        String subject = (String) value;
        if (subject.indexOf("Select a subject") != -1) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("Please select a subject.");
            context.addMessage(toValidate.getClientId(context), message);
            hasValidationErrors = true;
        }
    }
    

    public void validateCaptcha(FacesContext context,
            UIComponent toValidate,
            Object value) {

        if (c != null) {
            Map map = context.getExternalContext().getRequestParameterMap();
            String challenge = map.get("recaptcha_challenge_field").toString();
            String response = map.get("recaptcha_response_field").toString();
            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            ReCaptchaResponse resp = r.checkAnswer(req.getRemoteAddr(), challenge, response); 
            if (!resp.isValid() || hasValidationErrors ) {
                Logger.getLogger(ContactUsPage.class.getName()).info("INVALID RESPONSE: "+resp.getErrorMessage());
                ((UIInput) toValidate).setValid(false);
                if (hasValidationErrors){
                    context.addMessage(toValidate.getClientId(context), new FacesMessage("Some required information was entered incorrectly. Please press refresh below to get a new challenge, then correct the issue."));
                    hasValidationErrors = false;
                }
                else{
                    context.addMessage(toValidate.getClientId(context), new FacesMessage("Press refresh below to get a new challenge."));
                    hasValidationErrors = false;
                }
                
            }
        }
    }

    public String getCaptcha() {
        c = captchService.findCaptcha();
        String retVal = null;
        if (c != null) {
            r = new ReCaptchaImpl();
            l = new SimpleHttpLoader();

            r.setIncludeNoscript(true);
            r.setRecaptchaServer(ReCaptchaImpl.HTTPS_SERVER);
            r.setHttpLoader(l);
            r.setPrivateKey(c.getPrivateKey());
            r.setPublicKey(c.getPublicKey());
            Logger.getLogger(ContactUsPage.class.getName()).info("PUBLIC: "+c.getPublicKey()+" -- PRIVATE: "+c.getPrivateKey()+" -- HOST: "+c.getHost());
            retVal = r.createRecaptchaHtml(null, null);

        } else {
            retVal = "";
        }
        return retVal;
    }
}

