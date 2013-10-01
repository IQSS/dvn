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
 * PermissionBean.java
 *
 * Created on November 3, 2006, 11:37 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.VDCUser;

/**
 *
 * @author Ellen Kraffmiller
 */
public class PermissionBean  implements java.io.Serializable {
    
    /** Creates a new instance of PermissionBean */
    public PermissionBean() {
    }

    public PermissionBean(VDCUser user) {
        this.user=user;
    }
    
    public PermissionBean(UserGroup group) {
        this.group=group;
    }
    /**
     * Holds value of property checked.
     */
    private boolean checked;

    /**
     * Getter for property removePermission.
     * @return Value of property removePermission.
     */
    public boolean isChecked() {
        return this.checked;
    }

    /**
     * Setter for property removePermission.
     * @param removePermission New value of property removePermission.
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
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
    
}
