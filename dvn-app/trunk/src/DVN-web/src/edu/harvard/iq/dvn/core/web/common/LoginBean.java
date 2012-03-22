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
 * LoginBean.java
 *
 * Created on October 18, 2006, 5:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.common;

import edu.harvard.iq.dvn.core.admin.NetworkRoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.RoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCRole;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;

/**
 *
 * @author Ellen Kraffmiller
 */
public class LoginBean  implements java.io.Serializable {
   
    /**
     * Creates a new instance of LoginBean
     */
    public LoginBean() {
    }
    
    
    
    /**
     * Holds value of property user.
     */
    private VDCUser user;
    
    /**
     * Getter for property VDCUser.
     * @return Value of property VDCUser.
     */
    public VDCUser getUser() {
        return this.user;
    }
    
    /**
     * Setter for property VDCUser.
     * @param VDCUser New value of property VDCUser.
     */
    public void setUser(VDCUser user) {
        this.user = user;
    }
    
    
    /**
     * Getter for property currentVDC.
     * @return Value of property currentVDC.
     */
    public VDC getCurrentVDC() {
        //TODO: we need to fina a better way of getting the VDCRequest bean as this invloves a deprecated method
        // as we plan to revamp the login / authorization component, will leave for then
        VDCRequestBean vdcRequestBean = (VDCRequestBean) FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), "VDCRequest");

        
        if (vdcRequestBean==null) {
            return null;
        } else {
            return vdcRequestBean.getCurrentVDC();
        }
    }
    
    public  String getRoleName() {
        
        String roleName="user";
        VDCRole role = getVDCRole();
        if (role!=null) {
            roleName = role.getRole().getName();
        }
        
        return roleName;
    }
    
    public VDCRole getVDCRole() {
        VDCRole role= null;
        if (getCurrentVDC()!=null) {
            role = user.getVDCRole(getCurrentVDC());
          
        }
        return role;
    }
    
    
    public VDCRole getVDCRole(VDC vdc) {
        return user.getVDCRole(vdc);
    }

    public boolean isContributor() {
        return getRoleName().equals(RoleServiceLocal.CONTRIBUTOR);
    }

    public boolean isCurator() {
        return getRoleName().equals(RoleServiceLocal.CURATOR);
    }
    
    public boolean isAdmin() {
        return getRoleName().equals(RoleServiceLocal.ADMIN);        
    }

    public boolean isContributorOrAbove() {
        return getRoleName().equals(RoleServiceLocal.CONTRIBUTOR) || getRoleName().equals(RoleServiceLocal.CURATOR) || getRoleName().equals(RoleServiceLocal.ADMIN);
    }

    public boolean isCuratorOrAbove() {
        return getRoleName().equals(RoleServiceLocal.CURATOR) || getRoleName().equals(RoleServiceLocal.ADMIN);
    }

    public boolean isPrivilegedViewer() {
        return getRoleName().equals(RoleServiceLocal.PRIVILEGED_VIEWER);
    }
    
    public boolean isNetworkAdmin() {
        return this.user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN);
    }
    
    public boolean isNetworkCreator() {
        return this.user.getNetworkRole()!=null && user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.CREATOR);
    }
    
    // Basic user is a user with no roles or with the Privileged Viewer role only
    public boolean isBasicUser() {
        return (!isNetworkCreator() && !isNetworkAdmin() && !isAdmin() && !isCurator() && !isContributor());
    }

    private Map termsfUseMap = new HashMap();

    public Map getTermsfUseMap() {
        return termsfUseMap;
    }

    public void setTermsfUseMap(Map termsfUseMap) {
        this.termsfUseMap = termsfUseMap;
    }
   
    public boolean isHasDataverses() {
        boolean hasDataverses = false;
        VDCServiceLocal vdcService = null;
        try {
            vdcService = (VDCServiceLocal) new InitialContext().lookup("java:comp/env/vdcService");
        } catch (Exception e) {
            e.printStackTrace();
        }
        List vdcs = vdcService.getUserVDCs(user.getId());
        if (vdcs != null && !vdcs.isEmpty()) {
            hasDataverses = true;
        }
        return hasDataverses;
    }

    public boolean isHasContributed() {
        boolean hasContributed = false;
        UserServiceLocal userService = null;
        try {
            userService = (UserServiceLocal) new InitialContext().lookup("java:comp/env/vdcUserService");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // make sure we get current user from db
        hasContributed = userService.hasUserContributed(user.getId());
        return hasContributed;
    }

}
