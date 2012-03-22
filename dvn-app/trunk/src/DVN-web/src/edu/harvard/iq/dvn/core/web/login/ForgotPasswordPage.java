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
 * AddAccountPage.java
 *
 * Created on October 4, 2006, 1:04 PM
 */
package edu.harvard.iq.dvn.core.web.login;

import edu.harvard.iq.dvn.core.admin.EditUserService;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.web.common.StatusMessage;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.FacesException;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@ViewScoped
@Named("ForgotPasswordPage")
public class ForgotPasswordPage extends VDCBaseBean implements java.io.Serializable  {
    
    @EJB UserServiceLocal userService;
    String userName;
    
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public ForgotPasswordPage() {
    }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    
    public void validateUserName(FacesContext context, 
                          UIComponent toValidate,
                          Object value) {
 
    String name = (String) value;
    VDCUser user = userService.findByUserName(name);
   
    if (user==null) {
       ((UIInput)toValidate).setValid(false);
       
        FacesMessage message = new FacesMessage("No user account was found for this username.");
        context.addMessage(toValidate.getClientId(context), message);
    }

}
    
    public String submit() {
        VDCUser user = userService.findByUserName(this.userName);
        userService.updatePassword(user.getId());

        getVDCRenderBean().getFlash().put("successMessage","Your new password has been sent to the email address associated with this account.");

        if (getVDCRequestBean().getCurrentVDC() != null) {
            return "/StudyListingPage?faces-redirect=true" + getNavigationVDCSuffix();   
        } else {
            return "/HomePage?faces-redirect=true";
        }
    }
    
  
   
 
}

