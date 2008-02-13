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

package edu.harvard.hmdc.vdcnet.web.login;

import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import javax.ejb.EJB;

/**
 *
 * @author Gustavo Durand
 */
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

  

}
