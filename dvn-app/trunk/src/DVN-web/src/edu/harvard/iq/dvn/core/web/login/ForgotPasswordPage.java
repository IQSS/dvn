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

        getExternalContext().getFlash().put("successMessage","Your new password has been sent to the email address associated with this account.");

        if (getVDCRequestBean().getCurrentVDC() != null) {
            return "/StudyListingPage?faces-redirect=true" + getNavigationVDCSuffix();   
        } else {
            return "/HomePage?faces-redirect=true";
        }
    }
    
  
   
 
}

