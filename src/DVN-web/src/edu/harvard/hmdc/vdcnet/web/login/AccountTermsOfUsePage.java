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
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.component.html.HtmlInputHidden;

/**
 *
 * @author Gustavo Durand
 */
public class AccountTermsOfUsePage extends VDCBaseBean {
    @EJB UserServiceLocal userService; 
    public AccountTermsOfUsePage() {}
    
  
   
    

  

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
            VDCUser user = (VDCUser)sessionGet("loginUser");
            String loginWorkflow = (String)sessionGet("loginWorkflow");
            Long studyId = (Long)sessionGet("loginStudyId");
            String forward=null;
            if (user!=null) {
                if (termsAccepted) {
                    if (loginWorkflow!=null && loginWorkflow.equals("creator")) {
                        userService.makeCreator(user.getId());
                        
                    }
                    userService.setAgreedTermsOfUse(user.getId(), termsAccepted);
                    user.setAgreedTermsOfUse(termsAccepted);  // update detached object because it will be added to the loginBean
                    LoginPage.updateSessionForLogin(getExternalContext(),  getSessionMap(), getVDCSessionBean(), user);
                    LoginPage.setLoginRedirect(getSessionMap(), this.getExternalContext().getRequestContextPath(),getVDCRequestBean().getCurrentVDC(), loginWorkflow,studyId);
                }
              
            }    
         
            sessionRemove("loginWorkflow");
            sessionRemove("loginStudyId");
            sessionRemove("loginUser");
            forward="home";
           
            return forward;      
        
    }    

  

}
