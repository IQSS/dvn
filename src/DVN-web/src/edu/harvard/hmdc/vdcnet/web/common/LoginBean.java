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
 * LoginBean.java
 *
 * Created on October 18, 2006, 5:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.common;

import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.RoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCRole;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
        if (VDCBaseBean.getVDCRequestBean()==null) {
            return null;
        } else {
            return VDCBaseBean.getVDCRequestBean().getCurrentVDC();
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
            role = getVDCRole(getCurrentVDC());
          
        }
        return role;
    }
    
    
      public VDCRole getVDCRole(VDC vdc) {
        VDCRole role= null;
        if (vdc!=null) {
            for (Iterator it = user.getVdcRoles().iterator(); it.hasNext();) {
                VDCRole elem = (VDCRole) it.next();
                if (elem.getVdc().getId().equals(vdc.getId())) {
                    role = elem;
                    break;
                }              
            }           
        }
        return role;
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
    
  public boolean gethasDataverses() {
        boolean hasDataverses=false;
        List vdcs;
            VDCServiceLocal vdcService = null;
            try {
                vdcService = (VDCServiceLocal) new InitialContext().lookup("java:comp/env/vdcService");
            } catch (Exception e) {
                e.printStackTrace();
            }
           vdcs = vdcService.getUserVDCs(user.getId());
           if (vdcs!=null && !vdcs.isEmpty()) {
               hasDataverses=true;
           }
        return hasDataverses;
    }   
    
}
