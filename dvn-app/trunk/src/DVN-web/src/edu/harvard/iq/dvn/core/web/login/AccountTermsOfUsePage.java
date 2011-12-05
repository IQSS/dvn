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
@Named("AccountUseTermsPage")
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
