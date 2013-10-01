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
