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

package edu.harvard.iq.dvn.core.web.networkAdmin;

import edu.harvard.iq.dvn.core.admin.LoginDomain;
import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import java.util.Iterator;
/*
 * UserGroupsInfoBean.java
 *
 * Created on November 1, 2006, 12:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Ellen Kraffmiller
 */
public class UserGroupsInfoBean  implements java.io.Serializable {
    
    /** Creates a new instance of UserGroupsInfoBean */
    public UserGroupsInfoBean() {
    }
    
    public UserGroupsInfoBean(UserGroup group) {
        this.group = group;
    }

    /**
     * Holds value of property group.
     */
    private UserGroup group;

    /**
     * Getter for property group.
     * @return Value of property group.
     */
    public UserGroup getGroup() {
        return this.group;
    }

    /**
     * Setter for property group.
     * @param group New value of property group.
     */
    public void setGroup(UserGroup group) {
        this.group = group;
    }

   
    /**
     * Getter for property details.
     * @return Value of property details.
     */
    public String getDetails() {
        String str = "";
        for (Iterator it = group.getLoginDomains().iterator(); it.hasNext();) {
            LoginDomain elem = (LoginDomain) it.next();
            str+=elem.getIpAddress();
            if (it.hasNext()) {
                str+=", ";
            }   
        }
        if (group.getLoginDomains().size()>0 && group.getUsers().size()>0) {
            str+="; ";
        }
        for (Iterator it2 = group.getUsers().iterator(); it2.hasNext();) {
            VDCUser elem = (VDCUser) it2.next();
            str+=elem.getUserName();
            if (it2.hasNext()) {
                str+=", ";
            }   
        }
        return str;
    }

  
    
}
