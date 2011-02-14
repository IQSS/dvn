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
 * AllUsersDataBean.java
 *
 * Created on October 27, 2006, 12:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.networkAdmin;

import edu.harvard.iq.dvn.core.admin.NetworkRoleServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCRole;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import java.util.Iterator;

/**
 *
 * @author Ellen Kraffmiller
 */
public class AllUsersDataBean implements java.io.Serializable  {
    
    /**
     * Creates a new instance of AllUsersDataBean
     */
    public AllUsersDataBean() {
    }

    /**
     * Holds value of property user.
     */
    private VDCUser user;

    /**
     * Getter for property user.
     * @return Value of property user.
     */
    public VDCUser getUser() {
        return this.user;
    }

    /**
     * Setter for property user.
     * @param user New value of property user.
     */
    public void setUser(VDCUser user) {
        this.user = user;
    }
    
    public String getRoles() {
        
        String str="";
        if (user.getNetworkRole()!=null) {
            if (user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN)) {
                str+= " Network Admin";
                return str;
            } else if (user.getNetworkRole().getName().equals(NetworkRoleServiceLocal.CREATOR)){
                str+=" Dataverse Creator";
            } else {
                str+=" "+user.getNetworkRole().getName();
            }
        }
        if (user.getVdcRoles().size()>0 && user.getNetworkRole()!=null) {
            str+="; ";
        }
        for (Iterator<VDCRole> it2 = user.getVdcRoles().iterator(); it2.hasNext();) { 
            VDCRole role =  it2.next();
            str+= role.getVdc().getName()+" "+role.getRole().getName();
            if (it2.hasNext()) {
                str+=", ";
            }
            
        }
       return str;
    }
    
   

    public AllUsersDataBean(VDCUser user, boolean defaultNetworkAdmin) {
        this.user=user; 
        this.defaultNetworkAdmin = defaultNetworkAdmin;
       
    } 

    private boolean defaultNetworkAdmin;
  
    /**
     * Getter for property defaultNetworkAdmin.
     * @return Value of property defaultNetworkAdmin.
     */
    public boolean isDefaultNetworkAdmin() {
        return defaultNetworkAdmin;
    }

   
}
