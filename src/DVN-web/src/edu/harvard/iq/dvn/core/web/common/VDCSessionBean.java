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
 * VDCSessionBean.java
 *
 * Created on October 29, 2005, 6:37 PM
 */
package edu.harvard.iq.dvn.core.web.common;
import edu.harvard.iq.dvn.core.admin.GroupServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
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
    @EJB VDCNetworkServiceLocal vdcNetworkService; 

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

    public Map getGuestbookResponseMap() {
        return guestbookResponseMap;
    }

    public void setGuestbookResponseMap(Map guestbookResponseMap) {
        this.guestbookResponseMap = guestbookResponseMap;
    }

    private Map termsfUseMap = new HashMap();

    public Map getTermsfUseMap() {
        return termsfUseMap;
    }

    public void setTermsfUseMap(Map termsfUseMap) {
        this.termsfUseMap = termsfUseMap;
    } 
    
    private Map guestbookResponseMap = new HashMap();
    
    
    
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

     /**
     * Holds value of property vdcNetwork.
     */
    private VDCNetwork vdcNetwork;

    /**
     * Getter for property vdcNetwork.
     * @return Value of property vdcNetwork.
     */
    public VDCNetwork getVdcNetwork() {
        if (vdcNetwork == null) {
            System.out.print("vdcnetwork is null");
            VDCNetwork network = vdcNetworkService.findRootNetwork();  // There is only one network, which will always have Id=1
            setVdcNetwork(network);              
        }

        return this.vdcNetwork;
    }

    /**
     * Setter for property vdcNetwork.
     * @param vdcNetwork New value of property vdcNetwork.
     */
    public void setVdcNetwork(VDCNetwork vdcNetwork) {
        System.out.print("setting vdcnetwork");
        this.vdcNetwork = vdcNetwork;
    }
    
    
}
