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
 * StudyPage.java
 *
 * Created on September 19, 2006, 2:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.login;

import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author Gustavo Durand
 */

@ViewScoped
@Named("AccountTermsOfUsePage")
public class AccountTermsOfUsePage extends VDCBaseBean implements java.io.Serializable  {
    @EJB UserServiceLocal userService;
    private String termsOfUse; 
    @EJB VDCNetworkServiceLocal vdcNetworkService;
 
    public AccountTermsOfUsePage() {}

    public String getTermsOfUse() {
        return vdcNetworkService.find().getTermsOfUse();
        
    }

  

    public void init() {
  
        super.init();
     
    }       
    
    
    // only one checkbox for all terms
    //private boolean vdcTermsAccepted;
    //private boolean studyTermsAccepted;
    private boolean termsAccepted;
    
    

    public boolean isTermsAccepted() {
        return termsAccepted;
    }

    public void setTermsAccepted(boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }    
    
    public String acceptTerms_action () {
    
            LoginWorkflowBean loginWorkflowBean = (LoginWorkflowBean)this.getBean("LoginWorkflowBean");
            String forward = loginWorkflowBean.processTermsOfUse(termsAccepted);
        
            return forward;      
        
    }    

    public void validateTermsAccepted(FacesContext context,
            UIComponent toValidate,
            Object value) {
      
        Boolean acceptedValue = (Boolean) value;
        if (acceptedValue.booleanValue()==false) {
            ((UIInput)toValidate).setValid(false);
            FacesMessage message = new FacesMessage("You must accept the terms of use to continue.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
    
 

}
