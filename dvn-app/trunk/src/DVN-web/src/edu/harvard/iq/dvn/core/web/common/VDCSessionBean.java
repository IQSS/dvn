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
 * VDCSessionBean.java
 *
 * Created on October 29, 2005, 6:37 PM
 */
package edu.harvard.iq.dvn.core.web.common;
import edu.harvard.iq.dvn.core.admin.GroupServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

/**
 * <p>Session scope data bean for your application.  Create properties
 *  here to represent cached data that should be made available across
 *  multiple HTTP requests for an individual user.</p>
 *
 * <p>An instance of this class will be created for you automatically,
 * the first time your application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 */
@Named("VDCSession")
@SessionScoped
public class VDCSessionBean  implements java.io.Serializable  {
    @EJB GroupServiceLocal groupServiceLocal; 
 

    /**
     * Holds value of property loginBean.
     */
    private LoginBean loginBean;
    
    /**
     * Getter for property loginBean.
     * @return Value of property loginBean.
     */
    public LoginBean getLoginBean() {
        return this.loginBean;
    }

    /**
     * Setter for property loginBean.
     * @param loginBean New value of property loginBean.
     */
    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    private Map termsfUseMap = new HashMap();

    public Map getTermsfUseMap() {
        return termsfUseMap;
    }

    public void setTermsfUseMap(Map termsfUseMap) {
        this.termsfUseMap = termsfUseMap;
    } 
    
    public VDCUser getUser() {
        if (loginBean != null) {
            return loginBean.getUser();
        }
        
        return null;
    }

 

    /**
     * Getter for property ipUserGroup.
     * @return Value of property ipUserGroup.
     */
    public UserGroup getIpUserGroup() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        if (session.getAttribute("ipUserGroup") != null) {
            return (UserGroup) session.getAttribute("ipUserGroup");
        } else {
            return null;
        }
    }

 
    
    
}
